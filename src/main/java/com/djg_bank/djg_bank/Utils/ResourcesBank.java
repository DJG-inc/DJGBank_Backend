package com.djg_bank.djg_bank.Utils;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Component
public class ResourcesBank {
    public boolean isValidCardType(String cardType) {
        // Validar que el tipo de tarjeta sea "Visa" o "MasterCard"
        if (cardType == null) {
            return true;
        }
        else return !cardType.equalsIgnoreCase("Visa") && !cardType.equalsIgnoreCase("MasterCard");
    }

    public String generateValidCardNumber(String cardType) {
        // Generar un número de tarjeta válido según el tipo seleccionado (Visa o MasterCard)
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        cardNumber.append(cardType.equalsIgnoreCase("Visa") ? "4" : "5"); // Prefijo para Visa o MasterCard

        // Generar los 14 dígitos restantes (en lugar de 15)
        for (int i = 0; i < 14; i++) {
            cardNumber.append(random.nextInt(10)); // Generar los 14 dígitos restantes
        }

        // Aplicar el algoritmo de Luhn para generar el dígito de verificación
        cardNumber.append(generateLuhnDigit(cardNumber.toString()));

        // Añadir un dígito adicional para asegurar que haya 16 dígitos
        cardNumber.append(random.nextInt(10));

        return cardNumber.toString();
    }


    public int generateLuhnDigit(String cardNumber) {
        // Implementar el algoritmo de Luhn para generar el dígito de verificación
        int sum = 0;
        boolean doubleDigit = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return (sum * 9) % 10;
    }

    public Date generateExpiryDate() {
        // Generar la fecha de vencimiento como 5 años a partir de la fecha actual
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 5);
        return calendar.getTime();
    }

    public String generateRandomCVV() {
        // Generar un CVV aleatorio de tres dígitos
        Random random = new Random();
        int cvv = random.nextInt(1000); // Número aleatorio de 0 a 999
        return String.format("%03d", cvv); // Formatear como cadena de tres dígitos
    }

    public int calculateAge(Date dateOfBirth) {
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);

        Calendar currentDate = Calendar.getInstance();

        int age = currentDate.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (currentDate.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
}
