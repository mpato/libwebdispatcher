package org.metagarfus.webdispatcher;

import org.reflections.Configuration;

import java.util.ArrayList;
import java.util.List;

public class WebDispatcherConfig {

    public class Application {
        public String webContentPackage;
        private Application(){;
        }
    }

    public class HTTPServer {
        public String name = "LibWebDispatcherServer/1.0";
        public int port = 8085;
        public boolean isDaemon = false;

        private HTTPServer(){;
        }
    }

    public class Locations {
        public String resources = "resources";
        private Locations(){;
        }
    }

    public class Files {
        public String log = "log.txt";
        private Files(){;
        }
    }

    public class Logs {
        public List<String> ignore = new ArrayList<>();
        private Logs() {
        }
    }

    public final Application application = new Application();
    public final Locations locations = new Locations();
    public final Files files = new Files();
    public final Logs logs = new Logs();
    public final HTTPServer server = new HTTPServer();
}
