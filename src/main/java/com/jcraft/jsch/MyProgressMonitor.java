package com.jcraft.jsch;


public class MyProgressMonitor implements SftpProgressMonitor {
    private long transfered;

    public boolean count(long count) {
        transfered = transfered + count;
        System.out.println("Currently transferred total size: " + transfered + " bytes");
        return true;
    }

    public void end() {
        System.out.println("Transferring done.");
    }

    public void init(int op, String src, String dest, long max) {
        System.out.println("Transferring begin.");
    }
}