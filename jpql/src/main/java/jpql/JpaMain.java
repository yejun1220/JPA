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

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(20);
            em.persist(member1);
            em.persist(member2);

            System.out.println("=====================");

            List<Object[]> result = em.createQuery("select m.username, m.age From Member m").getResultList(); // 배열의 한 요소가 Object 타입(엔티티)

            for (Object[] objects : result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[0] = " + objects[1]);
            }

            System.out.println("=====================");

            Member result2 = em.createQuery("select m From Member m where m.username = :username", Member.class)
                    .setParameter("username", member1.getUsername())
                    .getSingleResult();

            System.out.println("username.getUsername() = " + result2.getUsername());

            System.out.println("=====================");

            List<MemberDTO> result3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) From Member m", MemberDTO.class).getResultList();
            MemberDTO memberDTO = result3.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
            memberDTO = result3.get(1);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

            for (MemberDTO dto : result3) {
                System.out.println("dto.getUsername() = " + dto.getUsername());
                System.out.println("dto.getAge() = " + dto.getAge());
            }


            tx.commit();
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

