package org.example.Dorms;

import java.io.IOException;

/**
 * This factory class generates mock or firebase dorm datasources
 */

public class DormDataSourceFactory {
  public enum DataSourceType {
    MOCK,
    FIREBASE
  }

  public static DormDataSource createDataSource(DataSourceType type) throws IOException {
    switch (type) {
      case MOCK:
        return new MockDormDataSource();
      case FIREBASE:
        return new FirebaseDormDatasource();
      default:
        throw new IllegalArgumentException("Unknown data source type");
    }
  }
}
