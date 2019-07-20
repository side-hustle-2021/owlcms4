package app.owlcms.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.VaadinServletRequest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class URLUtils {

    final private Logger logger = (Logger) LoggerFactory.getLogger(URLUtils.class);
    {
        logger.setLevel(Level.INFO);
    }

    ArrayList<String> wired = new ArrayList<>();
    ArrayList<String> recommended = new ArrayList<>();
    ArrayList<String> wireless = new ArrayList<>();
    ArrayList<String> loopback = new ArrayList<>();

    /**
     * @return the loopback
     */
    public ArrayList<String> getLocalUrl() {
        return loopback;
    }

    private boolean local;

    /**
     * Try to guess URLs that can reach the system.
     *
     * The browser on the master laptop most likely uses "localhost" in its URL. We
     * can't know which of its available IP addresses can actually reach the
     * application. We scan the network addresses, and try the URLs one by one,
     * listing wired interfaces first, and wireless interfaces second (in as much as
     * we can guess).
     *
     * We rely on the URL used to reach the "about" screen to know how the
     * application is named, what port is used, and which protocol works.
     *
     * @return HTML ("a" tags) for the various URLs that appear to work.
     */
    public URLUtils() {

        HttpServletRequest request = VaadinServletRequest.getCurrent().getHttpServletRequest();
        getRequestHeadersInMap(request);
        
        String prefix = "/META-INF/resources/";
        String targetFile = "sounds/timeOver.mp3"; //$NON-NLS-1$
        checkTargetFileOk(prefix, targetFile);

        String protocol = request.getScheme();
        int requestPort = request.getServerPort();
        String server = request.getServerName();
        String siteString = request.getRequestURI();
        String requestURL = request.getRequestURL().toString();

        local = isLocalAddress(server) || isLoopbackAddress(server);
        logger.trace("request {}", requestURL); //$NON-NLS-1$

        if (!local) {
            // we are logged on using a proper non-local URL, tell the users to use that.
            // return the URL we got, do not ask a cloud server for its interfaces
            recommended.add(requestURL);
            return;
        }

        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (//iface.isLoopback() ||
                        !iface.isUp())
                    continue;

                String displayName = iface.getDisplayName();
                String ifaceName = displayName.toLowerCase();

                // filter out interfaces to virtual machines
                if (!virtual(ifaceName)) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        ip = addr.getHostAddress();
                        boolean ipV4 = addr.getAddress().length == 4;
                        if (ipV4) {
                            logger.debug("address:{} local:{} ipv4:{} interface:{}", ip, addr.isSiteLocalAddress(),
                                    ipV4, ifaceName);
                            // try reaching the current IP address with the known protocol, port and site.
                            testIP(protocol, requestPort, siteString, targetFile, ip, ifaceName);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.error(LoggerUtils.stackTrace(e));
        }
        logger.trace("wired = {} {}", wired, wired.size()); //$NON-NLS-1$
        logger.trace("wireless = {} {}", wireless, wireless.size()); //$NON-NLS-1$
    }

    private void checkTargetFileOk(String prefix, String targetFile) {
        InputStream targetResource = this.getClass().getResourceAsStream(prefix + targetFile); // $NON-NLS-1$
        if (targetResource == null) {
            throw new RuntimeException("test resource not found " + targetFile); //$NON-NLS-1$
        }
    }

    private boolean virtual(String ifaceName) {
        return ifaceName.contains("virtual");
    }

    private void testIP(String protocol, int requestPort, String uri, String targetFile, String ip, String ifaceName) {
        try {
            URL siteURL = new URL(protocol, ip, requestPort, uri);
            String siteExternalForm = siteURL.toExternalForm();
            
            // use a file inside the site to avoid triggering a loop if called on home page
            URL testingURL = new URL(protocol, ip, requestPort, uri+targetFile);
            String testingExternalForm = testingURL.toExternalForm();

            HttpURLConnection huc = (HttpURLConnection) testingURL.openConnection();
            huc.setRequestMethod("GET"); //$NON-NLS-1$
            huc.connect();
            int response = huc.getResponseCode();

            if (response != 200) {
                logger.debug("{} not reachable: {}", testingExternalForm, response); //$NON-NLS-1$
            } else {
                logger.debug("{} OK: {}", testingURL, ifaceName); //$NON-NLS-1$
                if (isLoopbackAddress(ip)) {
                    loopback.add(siteExternalForm);
                } else if (ifaceName.contains("wireless")) { //$NON-NLS-1$
                    wireless.add(siteExternalForm);
                } else {
                    wired.add(siteExternalForm);
                }
            }
        } catch (Exception e) {
            logger.error(LoggerUtils.stackTrace(e));
        }
    }

    /**
     * @return the wired urls
     */
    public ArrayList<String> getWired() {
        return wired;
    }

    /**
     * @return the wireless urls
     */
    public ArrayList<String> getWireless() {
        return wireless;
    }

    /**
     * @return the external (non-local) url used to get to the site.
     */
    public ArrayList<String> getRecommended() {
        return recommended;
    }

    /**
     * @param serverString
     * @return true if address on a local network (not routed to the internet, not a loopback)
     */
    private boolean isLocalAddress(String serverString) {
        boolean isLocal = false;
        if (serverString.startsWith("10.") || serverString.startsWith("192.168")) { //$NON-NLS-1$ //$NON-NLS-2$
            isLocal = true;
        } else if (serverString.startsWith("172.")) { //$NON-NLS-1$
            serverString = serverString.substring(4);
            int sub = serverString.indexOf("."); //$NON-NLS-1$
            if (sub == -1) {
                isLocal = false;
            } else {
                try {
                    int subnet = Integer.parseInt(serverString.substring(0, sub));
                    isLocal = subnet >= 16 && subnet <= 31;
                } catch (NumberFormatException e) {
                    isLocal = false;
                }
            }

        } else {
            isLocal = false;
        }
        return isLocal;
    }
    
    private boolean isLoopbackAddress(String serverString) {
        return (serverString.toLowerCase().startsWith("localhost") || serverString.startsWith("127.0.0"));
    }

    public Map<String, String> getRequestHeadersInMap(HttpServletRequest request) {

        Map<String, String> result = new HashMap<>();
        String remoteAddr = request.getRemoteAddr();
        logger.debug("remoteAddr: {}", remoteAddr); //$NON-NLS-1$
        result.put("remoteAddr", remoteAddr); //$NON-NLS-1$
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement().toLowerCase();
            if (key.equals("x-forwarded-for") || key.equals("host")) { //$NON-NLS-1$ //$NON-NLS-2$
                String value = request.getHeader(key);
                result.put(key, value);
                logger.debug(key + ": " + value); //$NON-NLS-1$
            }
        }

        return result;
    }

}
