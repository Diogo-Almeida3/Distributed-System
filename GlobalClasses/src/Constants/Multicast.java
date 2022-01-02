package Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public abstract class Multicast {
    public static final String MULTICAST_GRDS_IP_DIFFUSION = "239.1.2.3"; // ip multicast to send information from the grds to the servers
    public static final int MULTICAST_GRDS_PORT_DIFFUSION = 9009; // port multicast to send information from the grds to the servers

    public static final String MULTICAST_GRDS_SEARCH_IP = "230.30.30.30"; // ip multicast to find grds port
    public static final int MULTICAST_GRDS_SEARCH_PORT = 3030; // port multicast to find grds port

    public static String getNetworkInterface() {
        try {
            final Enumeration<NetworkInterface> netifs = NetworkInterface.getNetworkInterfaces();

            // hostname is passed to your method
            InetAddress myAddr = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());


            while (netifs.hasMoreElements()) {
                NetworkInterface networkInterface = netifs.nextElement();
                Enumeration<InetAddress> inAddrs = networkInterface.getInetAddresses();
                while (inAddrs.hasMoreElements()) {
                    InetAddress inAddr = inAddrs.nextElement();
                    if (inAddr.equals(myAddr)) {
                        return networkInterface.getName();
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
