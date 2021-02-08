package org.bustram.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Cette classe est une classe d'assistance qui facilite l'utilisation des dates dans notre application
 */
public class DateHelper {

    /**
     * La méthode pour obtenir un objet date à partir d'une chaîne
     * @param value : La chaîne en question
     * @return : L'objet de la date
     */
    public static Date getDate(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * La méthode pour obtenir une chaîne à partir d'un objet date
     * @param value : La date en question
     * @return : La chaîne convertie
     */
    public static String getDate(Date value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(value);
    }

    /**
     * La méthode pour ajouter un nombre de mois à une certaine date
     * @param value : La date en question
     * @param count : Le nombre de mois à rajouter
     * @return : L'objet représentant la date après le nombre de mois souhaité
     */
    public static Date addMonths(Date value, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        calendar.add(Calendar.MONTH, count);
        return calendar.getTime();
    }

    /**
     * La méthode qui vérifie si une date est comprise entre deux dates données
     * @param startDate : Date de début
     * @param endDate : Date de fin
     * @param value : Date en question
     * @return : Une valeur booléenne indiquant si la date en question représente une date comprise entre la date début et la date fin
     */
    public static boolean during(Date startDate, Date endDate, Date value) {
        return startDate.compareTo(value) * value.compareTo(endDate) > 0;
    }
}
