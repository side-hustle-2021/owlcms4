package app.owlcms.data.customlogin;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.LoggerFactory;

import app.owlcms.data.jpa.JPAService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;

public class CustomUserRepository {
    private final static Logger logger = (Logger) LoggerFactory.getLogger(CustomUserRepository.class);
    static {
        logger.setLevel(Level.INFO);
    }
    
    public static void delete(CustomUser CustomUser) {
        JPAService.runInTransaction(em -> {
            em.remove(getById(CustomUser.getId(), em));
            return null;
        });
    }

    /**
     * Find all.
     *
     * @return the list
     */
    @SuppressWarnings("unchecked")
    public static List<CustomUser> findAll() {
        return JPAService.runInTransaction(em -> em.createQuery("select c from CustomUser c").getResultList());
    }

    /**
     * Gets CustomUser by id.
     *
     * @param id the id
     * @param em the em
     * @return the by id
     */
    @SuppressWarnings("unchecked")
    public static CustomUser getById(Integer id, EntityManager em) {
        Query query = em.createQuery("select u from CustomUser u where u.id=:id");
        query.setParameter("id", id);

        return (CustomUser) query.getResultList().stream().findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static CustomUser getByActivationCode(String activationCode) {

        JPAService.runInTransaction(em -> {
            Query query = em.createQuery("select u from CustomUser u where u.activationCode=:activationCode");
            query.setParameter("activationCode", activationCode);

            CustomUser nc = (CustomUser) query.getResultList().stream().findFirst().orElse(null);
            CustomUser.setCurrent(nc);
            return nc;
        });

        CustomUser current = CustomUser.getCurrent();
        return current;
    }

    @SuppressWarnings("unchecked")
    public static CustomUser getByUsername(String username) {

        JPAService.runInTransaction(em -> {
            Query query = em.createQuery("select u from CustomUser u where u.username=:username");
            query.setParameter("username", username);

            CustomUser nc = (CustomUser) query.getResultList().stream().findFirst().orElse(null);
            CustomUser.setCurrent(nc);
            return nc;
        });

        CustomUser current = CustomUser.getCurrent();
        return current;
    }

    /**
     * Save.
     *
     * @param customuser the CustomUser
     * @return the CustomUser
     */
    public static CustomUser save(CustomUser customuser) {
        JPAService.runInTransaction(em -> {
            CustomUser nc = em.merge(customuser);
            // needed because some classes get customuser parameters from getCurrent()
            CustomUser.setCurrent(nc);
            return nc;
        });

        CustomUser current = CustomUser.getCurrent();
        return current;
    }

    public static void createAdminIfNotExists(){
        CustomUser adminuser = getByUsername("admin");

        if (adminuser == null){
            System.out.println("Creating admin user ...");
            logger.info("Creating admin user ...");
            String password = System.getenv("ADMIN_PASSWORD");
            if (StringUtils.isEmpty(password)){
                System.out.println("Admin Password not set. It is null or empty");
                logger.error("Admin Password not set. It is null or empty");
                System.exit(-1);
            }
            CustomUserRepository.save(new CustomUser("admin", password, CustomRole.ADMIN));
            logger.info("Admin user created.");
        }
    }

}
