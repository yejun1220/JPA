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

            for(int i=0; i<2; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);

                Team team = new Team();
                team.setName("team" + i);
                em.persist(team);

                member.changeTeam(team);
                member.setTeam(team);

                member.changeTeam(team);
                em.persist(member);
            }

            em.flush();
            em.clear();

            em.createQuery("select (select avg(m1.age) from Member m1) as avgAge from Member m join Team t on m.username = t.name");


        }
        catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
        finally {
            em.close();
        }

        emf.close();
    }
}

