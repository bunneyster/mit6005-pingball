package pb.warmup;

import java.io.File;

import pb.cli.PingballClient;


/**
 * TODO: put documentation for your class here
 */
public class Main {
    
    /**
     * TODO: describe your main function's command line arguments here
     */
    public static void main(String[] args) {
    	PingballClient.runClient(new File("boards/warmup.pb"), null, 0);
    }
}