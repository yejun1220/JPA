package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            Member result = em.createQuery("select m From Member m where m.username = :username", Member.class)
                    .setParameter("username", member.getUsername())
                    .getSingleResult();

            System.out.println("username.getUsername = " + result.getUsername());

            tx.commit();
        }
        catch (Exception e) {
            System.out.println("e = " + e);
            tx.rollback();
        }
        finally {
            em.close();
        }

        emf.close();
    }
}

