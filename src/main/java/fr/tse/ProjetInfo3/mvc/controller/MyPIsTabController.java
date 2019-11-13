package fr.tse.ProjetInfo3.mvc.controller;

public class MyPIsTabController {
    private MainController mainController;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
