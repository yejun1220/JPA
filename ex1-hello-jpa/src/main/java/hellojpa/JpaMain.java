package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member1 = new Member();
            member1.setUsername("Member1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("Member2");
            em.persist(member2);

            em.flush();
            em.clear();



            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            System.out.println(e);
        }
        finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(Member m1, Member m2) {
        System.out.println("m1 == m2 = " + (m1 instanceof Member));
        System.out.println("m1 == m2 = " + (m2 instanceof Member));
    }
}

