package Constants;

public abstract class Multicast {
    public static final String MULTICAST_GRDS_IP_DIFFUSION = "239.1.2.3"; // ip multicast to send information from the grds to the servers
    public static final int MULTICAST_GRDS_PORT_DIFFUSION = 9009; // port multicast to send information from the grds to the servers

    public static final String MULTICAST_GRDS_SEARCH_IP = "230.30.30.30"; // ip multicast to find grds port
    public static final int MULTICAST_GRDS_SEARCH_PORT = 3030; // port multicast to find grds port
}
