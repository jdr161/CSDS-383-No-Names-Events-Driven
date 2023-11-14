package com.nonames.cli;

public class Main {
    public static void main(String[] args) {
        //conn = connect();
        //setupDatabase();

        CLICode code = CLICode.MAIN_MENU;
        while (code == CLICode.MAIN_MENU) {
            code = MainCLI.mainMenu();
        }

        //conn.close();
        System.exit(0);
    }
}
