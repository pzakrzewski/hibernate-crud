package pl.zakrzewski;

import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.zakrzewski.entity.Drug;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {
        DrugDao drugDao = new DrugDao();

        Drug firstdrug = new Drug();
        firstdrug.setName("Coffee");
        firstdrug.setPrice(new BigDecimal(15));
        firstdrug.setProdYear(date());
        firstdrug.setDrugType(DrugType.LEGAL);

        Drug secondDrug = new Drug();
        secondDrug.setName("Energy Drink");
        secondDrug.setPrice(new BigDecimal(5));
        secondDrug.setProdYear(date());
        secondDrug.setDrugType(DrugType.BOOSTERS);

        Drug thirdDrug = new Drug();
        thirdDrug.setName("Mephedrone");
        thirdDrug.setPrice(new BigDecimal(50));
        thirdDrug.setProdYear(date());
        thirdDrug.setDrugType(DrugType.HEAVY);

        optimisticLock();
        pessimisticLock();

        drugDao.saveDrug(firstdrug);
        drugDao.saveDrug(secondDrug);
        drugDao.saveDrug(thirdDrug);

        System.out.println(drugDao.readDrug(firstdrug.getId()));
        System.out.println(drugDao.readDrug(secondDrug.getId()));
        System.out.println(drugDao.readDrug(thirdDrug.getId()));
        drugDao.deleteDrug(firstdrug.getId());

        Drug newDrug = new Drug();
        newDrug.setId(secondDrug.getId());
        newDrug.setName("MDMA");
        newDrug.setPrice(new BigDecimal(25));
        newDrug.setProdYear(date());
        newDrug.setDrugType(DrugType.SOFT);
        drugDao.updateDrug(newDrug);

        List<Drug> drugsByPower = drugDao.getDrugsByPower(DrugType.SOFT);
        drugsByPower.forEach(System.out::println);
    }

    private static LocalDate date() {
        int hundredYears = 100 * 365;
        return LocalDate.ofEpochDay(ThreadLocalRandom
                .current().nextInt(-hundredYears, hundredYears));
    }

    private static void optimisticLock() {
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        Session session2 = HibernateUtil.getSessionFactory().openSession();

        Drug drug = new Drug();
        drug.setName("Crack");
        drug.setPrice(new BigDecimal(150));
        drug.setProdYear(date());
        drug.setDrugType(DrugType.HEAVY);

        DrugDao drugDao = new DrugDao();
        drugDao.saveDrug(drug);

        Drug readDrug1 = session1.get(Drug.class, drug.getId());
        Drug readDrug2 = session2.get(Drug.class, drug.getId());

        Transaction tx1 = session1.beginTransaction();
        Transaction tx2 = session2.beginTransaction();

        readDrug1.setName("White powder");
        readDrug2.setName("Cocaine");

        session1.update(readDrug1);
        tx1.commit();

        try {
            session2.update(readDrug2);
            tx2.commit();
        } catch (Exception e) {
            System.out.println("Optimistic locking failed: " + e.getMessage());
            tx2.rollback();
        } finally {
            session1.close();
            session2.close();
        }
    }

    private static void pessimisticLock() {
        // Step 1: Insert initial data
        Drug drug = new Drug();
        drug.setName("Crack");
        drug.setPrice(new BigDecimal(150));
        drug.setProdYear(date());
        drug.setDrugType(DrugType.HEAVY);

        DrugDao drugDao = new DrugDao();
        drugDao.saveDrug(drug);

        // Step 2: Session A locks the row
        Thread threadA = new Thread(() -> {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            System.out.println("Thread A: Locking product");
            Drug readDrug = session.find(Drug.class, drug.getId(), LockModeType.PESSIMISTIC_WRITE);
            readDrug.setName("White powder");
            try {
                Thread.sleep(5000);  // Hold the lock for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tx.commit();
            session.close();
            System.out.println("Thread A: Transaction committed");
        });

        // Step 3: Session B tries to update the same row while it's locked
        Thread threadB = new Thread(() -> {
            try {
                // Delay to make sure Thread A locks it first
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            try {
                System.out.println("Thread B: Trying to lock product");
                Drug readDrug = session.find(Drug.class, drug.getId(), LockModeType.PESSIMISTIC_WRITE);
                readDrug.setName("Cocaine");
                tx.commit();
                System.out.println("Thread B: Transaction committed");
            } catch (Exception e) {
                tx.rollback();
                System.out.println("Thread B: Failed to lock or update: " + e.getMessage());
            } finally {
                session.close();
            }
        });

        threadA.start();
        threadB.start();

        try {
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}