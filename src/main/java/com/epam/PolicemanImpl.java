package com.epam;

import javax.annotation.PostConstruct;

/**
 * @author Evgeny Borisov
 */
public class PolicemanImpl implements Policeman {

    @InjectByType
    private Recommendator recommendator;

    @PostConstruct
    public void init() {
        System.out.println("policeman was created");
        System.out.println(recommendator.getClass());
    }

    @Override
    @Deprecated
    public void makePeopleLeaveRoom() {
        System.out.println("пиф паф, бах бах, кыш, кыш!");
    }
}
