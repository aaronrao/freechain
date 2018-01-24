package com.freechain.block;

/**
 * @author aaron.rao
 *
 */
public class Main {
    public static void main(String[] args) {
        if (args != null && (args.length == 1)) {
            try {
                int httpPort = Integer.valueOf(args[0]);
                HTTPService httpService = new HTTPService(new BlockChain());
                httpService.initHTTPServer(httpPort);
            } catch (Exception e) {
                System.out.println("startup is error:" + e.getMessage());
            }
        } else {
            System.out.println("usage: java -jar freechain.jar 5000");
        }
    }
}
