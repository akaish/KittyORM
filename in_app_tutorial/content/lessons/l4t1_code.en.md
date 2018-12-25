---
title: "Lesson4 tab 1 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**Tip №1: Avoid using generating data model from packages**

1.  
<details> 
  <summary>Click to view static registry initialization via annotations example: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        databaseName = "basic_database",
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.basicdb"},
        logTag = LOG_TAG,
        isLoggingOn = true,
        isProductionOn = true,
        isPragmaOn = true
)
@KITTY_DATABASE_REGISTRY(
        // You can just define set of domain models and extended crud controllers (mappers) would
        // be added only if those models are annotated with KITTY_EXTENDED_CRUD annotation.
        // For example, for ComplexRandom.class it would be 
        // @KITTY_EXTENDED_CRUD(extendedCrudController = ComplexRandomMapper.class)
        domainModels = {
                ComplexRandomModel.class,
                IndexesAndConstraintsModel.class,
                RandomModel.class
        },
        // Or you can provide explicit assignment of model->mapper pairs by defining domainPais
        domainPairs = {
                @KITTY_REGISTRY_PAIR(model = ComplexRandomModel.class, mapper = ComplexRandomMapper.class),
                @KITTY_REGISTRY_PAIR(model = IndexesAndConstraintsModel.class),
                @KITTY_REGISTRY_PAIR(model = RandomModel.class, mapper = RandomMapper.class)
        }
)
public class BasicDatabase extends KittyDatabase {
    ...
}
  {{< /highlight >}} 
</details>
2. 
<details> 
  <summary>Click to view static registry initialization via overloading `getStaticRegistry()` method example: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        databaseName = "basic_database",
        logTag = LOG_TAG,
        isLoggingOn = true,
        isProductionOn = true,
        isPragmaOn = true
)
public class BasicDatabase extends KittyDatabase {

    public static final Map<Class<? extends KittyModel>, Class<? extends KittyMapper>> staticRegistry = new HashMap<>();

    static {
        staticRegistry.put(ComplexRandomModel.class, ComplexRandomMapper.class);
        staticRegistry.put(IndexesAndConstraintsModel.class, KittyMapper.class);
        staticRegistry.put(RandomModel.class, RandomMapper.class);
    }

    public static final String LOG_TAG = "BASIC DB DEMO";

    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     * <br> See {@link KittyDatabase#KittyDatabase(Context, String)} for more info.
     *
     * @param ctx
     */
    public BasicDatabase(Context ctx) {
        super(ctx);
    }

    @Override
    protected Map<Class, Class<KittyMapper>> getStaticRegistry() {
        return (Map) staticRegistry;
    }
}
  {{< /highlight >}} 
</details>

**Tip №2: Avoid multiply initialization of `KittyDatabase`**

1. 
<details> 
  <summary>Click to view KittyDatabase implementation static initialization at `Application` implementation example: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
public class AwesomeApplication extends Application {

	private static AwesomeKittyDatabaseImplementation database;

	public void onCreate() {
		super.onCreate();
		AwesomeApplication.database = new AwesomeKittyDatabaseImplementation(getApplicationContext());
	}

	public static AwesomeKittyDatabaseImplementation getAwesomeDB() {
		return database;
	}
}
  {{< /highlight >}} 
</details>

**Tip №3: Optimize your data model and statements**

1. 
<details> 
  <summary>Click to view `INDEX` declaration with KittyORM example: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cai")
@INDEX(indexColumns = {"creation_date"}) // Index declaration example №1
public class IndexesAndConstraintsModel extends KittyModel {


    ...

    @KITTY_COLUMN(columnOrder = 5)
    @DEFAULT(
            predefinedLiteralValue = LiteralValues.CURRENT_TIMESTAMP
    )
    // Index declaration example №2
    @ONE_COLUMN_INDEX(unique = true, indexName = "IAC_unique_index_creation_timestamp")
    @NOT_NULL
    public Timestamp creationTmstmp;

    ...
}
  {{< /highlight >}} 
</details>
2. 
<details> 
  <summary>Click to view bulk insert operation in transaction mode with KittyORM example: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
void insertIntoDB() {
    // Getting mapper instance and filling model collection with random models
    RNDRandomModelFactory rndFactory = new RNDRandomModelFactory(Lesson2Tab5Random.this.getContext());
    RandomMapper mapper = (RandomMapper) getDatabase().getMapper(RandomModel.class);
    List<RandomModel> toInsert = new ArrayList<>();
    for(int i = 0; i < 50000; i++)
        toInsert.add(rndFactory.newRandomModel());
            
     // Saving list of 50k models in transaction mode
     mapper.insertInTransaction(toInsert);
           
     // Closing mapper
     mapper.close();
}
  {{< /highlight >}} 
</details>

**Tip №6: Turn off logging at production**

1. 
<details> 
  <summary>Click to view logging settings example: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        isLoggingOn = false, // By default false, bootstrap and error logging
        isProductionOn = true, // By default true, when false - query logging
        isKittyDexUtilLoggingEnabled = false, // By default false, when true - a lot of logging about classes in app namespace at initialization
        databaseName = "mig",
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.migrations.migv4"},
        databaseVersion = 4,
        logTag = MigrationDBv4.LTAG
)
public class MigrationDBv4 extends KittyDatabase {
    ...
}
  {{< /highlight >}} 
</details>


**Tip №7: Run expensive operations not in UI thread**

1. 
<details> 
  <summary>Click to view example of KittyORM in `AsyncTask`: </summary>
  {{< highlight java "linenos=inline, linenostart=1">}}
    static final int INSERT_AMOUNT = 25;

    static final String ERR_INSERT_RND = "Lesson5tab1InsertRNDDataError, see exception details!";

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t1_running_requested_operation_pg_title),
                    getString(R.string._l5_t1_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
            if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                return new InsertRandomResults(
                        null,
                        -1l,
                        -1l,
                        -1l,
                        false,
                        getSf().currentMigDBVersion()
                );
            } else {
                try {
                    KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                    long recordsCount = mapper.countAll();
                    long affected = mapper.deleteAll();
                    LinkedList<MigOneModel> modelsToInsert = new LinkedList<>();
                    getSf().setDatabaseCreated(true);
                    getSf().setCurrentMigDBVersion(DB_IMPLEMENTATION_VERSION);
                    getSf().setDatabaseDeletedManually(false);
                    MigV1RandomModelFactory factory = new MigV1RandomModelFactory(getContext());
                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigOneModel m = factory.newM1RndModel();
                        modelsToInsert.addLast(m);
                    }
                    mapper.insertInTransaction(modelsToInsert);
                    List<MigOneModel> models = mapper.findAll();
                    long recordsCountAfter = mapper.countAll();
                    mapper.close();
                    Iterator<MigOneModel> mI = models.iterator();
                    LinkedList<String> out = new LinkedList<>();
                    while (mI.hasNext()) {
                        out.addLast(mI.next().toString());
                    }
                    return new InsertRandomResults(out, affected, recordsCount, recordsCountAfter, true, getSf().currentMigDBVersion());
                } catch (Exception e) {
                    Log.e(MigrationDBv1.LTAG, ERR_INSERT_RND, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv1.LTAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
                        }
                    }
                    return new InsertRandomResults(
                            null,
                            -1l,
                            -1l,
                            -1l,
                            false,
                            getSf().currentMigDBVersion()
                    );
                }
            }
        }

        @Override
        protected void onPostExecute(InsertRandomResults result) {
            dialog.cancel();
            if (eventsListView != null) {
                eventsListView.setAdapter(new BasicArrayAdapter(getContext(), new LinkedList<String>()));
                eventsListView.setOnTouchListener(new View.OnTouchListener() {

                    // Setting on Touch Listener for handling the touch inside ScrollView
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // Disallow the touch request for parent scroll on touch of child view
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                });
                if (result.operationSuccess) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t1_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t1_deleted_to_events), result.deletedModelsAffectedRows));
                    for (String modelString : result.modelInsertions) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t1_inserted_to_events), M1M1TN, modelString));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t1_count_to_events), result.modelsCountAfter));
                } else {
                    if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t1_error_event));
                    }
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }


    }

    class InsertRandomResults {
        List<String> modelInsertions;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;
        int dbVersion;

        public InsertRandomResults(List<String> modelInsertions, long deletedModelsAffectedRows,
                                   long modelsCountBefore, long modelsCountAfter, boolean opSuccess,
                                   int dbVersion) {
            this.modelInsertions = modelInsertions;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
            this.dbVersion = dbVersion;
        }
    }
  {{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
