import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {

    public static final String INITIAL_DATA_YAML = "initial-data.yml";

    @Override
    public void doJob() {
        // Check if the database is empty
        if (User.count() == 0) {
            Logger.info("Loading initial data from %s.", INITIAL_DATA_YAML);
            Fixtures.loadModels(INITIAL_DATA_YAML);
        }
    }

}