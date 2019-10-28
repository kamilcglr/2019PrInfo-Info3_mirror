package fr.tse.ProjetInfo3.mwp.controller;

public class UserTabController {
    private MainController mainController;

    /*Controller can acces to this Tab */
    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
