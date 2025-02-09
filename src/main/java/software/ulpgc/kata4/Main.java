package software.ulpgc.kata4;

import software.ulpgc.kata4.control.ImportCommand;
import software.ulpgc.kata4.ui.ImportDialog;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        new ImportCommand(ImportDialog()).execute();
    }

    private static ImportDialog ImportDialog() {
        return () -> new File("title.basics.tsv.gz");
    }
}
