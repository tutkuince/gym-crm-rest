package com.epam.gymcrm;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymCrmApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.epam.gymcrm");
        System.out.println("gym-crm started!");
        context.close();
    }
}
