package app.owlcms.data.customlogin;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.LoggerFactory;

import app.owlcms.data.jpa.JPAService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

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

}
