package loadBalancingProtocols;

import java.util.ArrayList;
import java.util.List;

public class loadBalancingParameters {
    public static int cyclePPS = 0;
    public static int cycleDB = 0;
    public static List<Double> loads_sumsList = new ArrayList<Double>();
    // grid parameter
    public static int m_height = 10;
    public static int n_width = 1000;

    // lollipop parameter
    public static int m_cliqueSize = 500;
    public static int n_PathSize = 500;

    // ring of clique parameter
    public static int m_cliqueAmount = 10;
    public static int n_CliqueSize = 1000;
}
