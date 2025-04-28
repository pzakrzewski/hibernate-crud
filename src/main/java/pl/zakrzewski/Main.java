package pl.zakrzewski;

import pl.zakrzewski.entity.Drug;

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

        drugDao.saveDrug(firstdrug);
        drugDao.saveDrug(secondDrug);
        drugDao.saveDrug(thirdDrug);

        System.out.println(drugDao.readDrug(1L));
        drugDao.deleteDrug(1L);

        Drug newDrug = new Drug();
        newDrug.setId(2L);
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

}