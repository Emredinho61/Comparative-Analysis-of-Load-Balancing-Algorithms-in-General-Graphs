package loadBalancingProtocols;

import java.util.ArrayList;
import java.util.List;

public class loadBalancingParameters {
    public static int cyclePPS = 0;
    public static int cycleDB = 0;
    public static List<Double> loads_sumsList = new ArrayList<Double>();
    // torus parameter
    public static int m_height = 4;
    public static int n_width = 4;

    // lollipop parameter
    public static int m_cliqueSize = 5000;
    public static int n_PathSize = 5000;

    // ring of clique parameter
    public static int m_cliqueAmount = 1000;
    public static int n_CliqueSize = 10;
}
