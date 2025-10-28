package com.mthree;

import com.mthree.controller.Controller;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        // Annotation Dependency Injection
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("com.mthree");
        ctx.refresh();

        // Start the controller
        Controller controller = ctx.getBean("controller", Controller.class);
        controller.run();
    }
}