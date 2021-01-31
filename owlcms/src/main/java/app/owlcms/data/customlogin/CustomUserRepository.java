package app.owlcms.data.customlogin;

import java.util.List;
import java.util.LinkedList;

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
    public static List<CustomUser> fetchUsers(int offset, int limit){
        return JPAService.runInTransaction(em -> 
            em.createNativeQuery("select * from CustomUser limit :limit offset :offset", CustomUser.class)
            .setParameter("limit", limit).setParameter("offset", offset)
            .getResultList()
        );
    }

    @SuppressWarnings("unchecked")
    public static int getUsersCount(){
        return (int) JPAService.runInTransaction(em -> 
            em.createQuery("select count(u) from CustomUser u")
            .getResultList().stream().findFirst().orElse(0))
        ;
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
            adminuser = CustomUserRepository.save(new CustomUser("admin", password, CustomRole.ADMIN));
            try {
                AuthService.activate(adminuser.getActivationCode());    
            } catch (Exception e) {
                logger.error("Invalid link. User not found");
            }
            logger.info("Admin user created.");
        }
    }

    public static List<CustomUser> findFiltered(String username, CustomRole role, Boolean active){
        return JPAService.runInTransaction(em -> {
            return filterQueryResults(em, username, role, active);
        });
    }

    @SuppressWarnings("unchecked")
    public static List<CustomUser> filterQueryResults(EntityManager em, String username, 
                CustomRole role, Boolean active){

        String filterQuery = "select c from CustomUser c ";
        List<String> whereList = new LinkedList<>();

        if (username != null && !username.isEmpty()){
            whereList.add("lower(c.username) like :username||'%'");
        }

        if (role != null){
            whereList.add("c.role=:role");
        }

        if (active != null){
            whereList.add("c.active=:active");
        }

        if (whereList.size() > 0) {
            String allWhere = String.join(" and ", whereList);
            filterQuery = filterQuery + " where " + allWhere;
        }

        Query query = em.createQuery(filterQuery);

        if (username != null && !username.isEmpty()){
            query.setParameter("username", username);
        }

        if (role != null){
            query.setParameter("role", role);
        }

        if (active != null){
            query.setParameter("active", active);
        }
        
        return query.getResultList();
    }

    public static void updateActive(CustomUser customuser){
        customuser.setActive(!customuser.isActive());

        JPAService.runInTransaction(em -> 
            em.createQuery("update CustomUser c set c.active=:active where c.id=:id")
            .setParameter("active", customuser.isActive()).setParameter("id", customuser.getId())
            .executeUpdate()
        );
    }
}
