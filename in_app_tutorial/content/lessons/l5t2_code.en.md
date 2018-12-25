---
title: "Lesson5 tab 2 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**KittyORM `mig` v.2 implementation sources**

1. 
<details> 
  <summary>Click to view `MigrationDBv2.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        isLoggingOn = true,
        isProductionOn = false,
        databaseName = "mig",
        databaseVersion = 2,
        logTag = MigrationDBv2.LTAG,
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.migrations.migv2"}
)
@KITTY_DATABASE_REGISTRY(
        domainModels = {
                net.akaish.kittyormdemo.sqlite.migrations.migv2.MigOneModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv2.MigTwoModel.class
        }
)
@KITTY_DATABASE_HELPER(
        onUpgradeBehavior = KITTY_DATABASE_HELPER.UpgradeBehavior.DROP_AND_CREATE
)
public class MigrationDBv2 extends KittyDatabase {

    public static final String LTAG = "MIGv2";

    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     *
     * @param ctx
     */
    public MigrationDBv2(Context ctx) {
        super(ctx);
    }
}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `MigOneModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(
        tableName = "mig_one"
)
public class MigOneModel extends KittyModel {
    @KITTY_COLUMN(
            columnOrder = 0,
            isIPK = true)
    public Long id;

    @KITTY_COLUMN(columnOrder = 1)
    @NOT_NULL
    @DEFAULT(predefinedLiteralValue = LiteralValues.CURRENT_DATE)
    public String creationDate;

    @KITTY_COLUMN(columnOrder = 2)
    @DEFAULT(signedInteger = 228)
    @ONE_COLUMN_INDEX(indexName = "m1_di_index")
    public Integer defaultInteger;

    @KITTY_COLUMN(columnOrder = 3)
    public Timestamp currentTimestamp;

    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; creationDate = ")
                .append(creationDate)
                .append(" ; someInteger = ")
                .append(defaultInteger)
                .append(" ; currentTimestamp = ")
                .append(currentTimestamp)
                .append(" ]").toString();
    }
}
{{< /highlight >}} 
</details>

3. 
<details> 
  <summary>Click to view `MigTwoModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(
        tableName = "mig_two"
)
@INDEX(
        indexName = "m2_sa_index",
        indexColumns = {"some_animal"}
)
public class MigTwoModel extends KittyModel {

    @KITTY_COLUMN(
            columnOrder = 0,
            isIPK = true
    )
    public Long id;

    @KITTY_COLUMN(
            columnOrder = 1
    )
    @FOREIGN_KEY(
            reference = @FOREIGN_KEY_REFERENCE(
                    foreignTableName = "mig_one",
                    foreignTableColumns = {"id"},
                    onUpdate = OnUpdateDeleteActions.CASCADE,
                    onDelete = OnUpdateDeleteActions.CASCADE
            )
    )
    public Long migOneReference;

    @KITTY_COLUMN(
            columnOrder = 2
    )
    public Animals someAnimal;

    @Override
    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; migOneReference = ")
                .append(migOneReference)
                .append(" ; someAnimal = ")
                .append(someAnimal)
                .append(" ] ").toString();
    }
}
{{< /highlight >}} 
</details>

**Fragment and utility code used in this tutorial**

1. 
<details> 
  <summary>Click to view `MigV2RandomModelFactory.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class MigV2RandomModelFactory {
    final Context ctx;
    final Random rnd;

    public MigV2RandomModelFactory(Context ctx) {
        this.ctx = ctx;
        this.rnd = new Random();
    }

    public MigOneModel newM1RndModel() {
        return newM1RndModel(rnd.nextBoolean());
    }

    public MigOneModel newM1RndModel(boolean setCDDefault) {
        MigOneModel model = new MigOneModel();
        if(setCDDefault)
            model.setFieldExclusion("creationDate");
        else
            model.creationDate = new Date(System.currentTimeMillis()).toString();
        model.defaultInteger = rnd.nextInt();
        model.currentTimestamp = new Timestamp(System.currentTimeMillis());
        return model;
    }

    public MigTwoModel newM2RndModel(ArrayList<MigOneModel> models) {
        int mlSize = models.size();
        return newM2RndModel(models.get(rnd.nextInt(mlSize)).id);
    }

    public MigTwoModel newM2RndModel(Long migOneReference) {
        MigTwoModel model = new MigTwoModel();
        model.someAnimal = Animals.rndAnimal(rnd);
        model.migOneReference = migOneReference;
        return model;
    }
    
}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `Lesson5Tab2DCMigrations.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class Lesson5Tab2DCMigrations extends Lesson5BaseFragment {

    private MigrationDBv2 databaseV2;
    private SharedPreferencesMigDB sf;

    private Button insertRandomButton;
    private Button clearTableButton;
    private Button deleteDatabaseButton;

    private ListView eventsListView;

    private TextView statusTV;

    private MigDatabaseState mdbState;


    final static int DB_IMPLEMENTATION_VERSION = 2;
    final static int TABLE_AMOUNT = 2;

    public Lesson5Tab2DCMigrations() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson5_tab2_dc_migration, container, false);

        insertRandomButton = rootView.findViewById(R.id.l5_t2_go_button);
        clearTableButton = rootView.findViewById(R.id.l5_t2_clear_button);
        deleteDatabaseButton = rootView.findViewById(R.id.l5_t2_delete_database_button);

        eventsListView = rootView.findViewById(R.id.l5_t2_actions);

        insertRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert25RND();
            }
        });

        clearTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTable();
            }
        });

        deleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase();
            }
        });

        statusTV = rootView.findViewById(R.id.l5_t2_status);


        setUpExpandedList(
                rootView,
                R.id._l5_t2_expanded_panel_list,
                R.id._l5_t2_expanded_panel_text,
                R.string._l5_t2_expanded_text_pattern
        );

        reloadTableExpandedList();
        reloadStatus();
        return rootView;
    }

    public MigDatabaseState getMdbState(Context context, int implVersion, String[] tables) {
        if(mdbState != null) return mdbState;
        mdbState = new MigDatabaseState(implVersion, tables, context, getSf());
        return mdbState;
    }

    public void reloadStatus() {
        if(statusTV != null) {
            statusTV.setText(getMdbState(getContext(), DB_IMPLEMENTATION_VERSION, new String[] {M1M1TN, M1M2TN}).toString());
        }
    }


    @Override
    public void onVisible() {
        reloadTableExpandedList();
        reloadStatus();
    }

    private SharedPreferencesMigDB getSf() {
        if(sf != null) return sf;
        sf = new SharedPreferencesMigDB(getContext());
        return sf;
    }

    private MigrationDBv2 getDatabase() {
        // retrieving existing database after upgrade -> downgrade would cause onUpgrade() script would be run after mapper fetching
        databaseV2 = new MigrationDBv2(getContext());
        return databaseV2;
    }

    private void insert25RND() {
        new InsertRandomAsync().execute(0l);
    }

    private void clearTable() {
        new WipeAsync().execute(0l);
    }

    private void deleteDatabase() {
        new DeleteDatabaseAsync().execute(0l);
    }

    private void reloadTableExpandedList() {
        new ReloadTableAsync().execute(0l);
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l5_t2_snackbar_message;
    }



    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<String>> {

        @Override
        protected List<String> doInBackground(Long... params) {
            LinkedList<String> toListView = new LinkedList<>();
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                List<MigOneModel> m1Models = mapper.findAll();
                mapper.close();
                KittyMapper mapperT2 = getDatabase().getMapper(MigTwoModel.class);
                List<MigTwoModel> m2Models = mapperT2.findAll();
                if(m1Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t2_m1_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t2_m1_db), m1Models.size()));
                    Iterator<MigOneModel> mI = m1Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m2Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t2_m2_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t2_m2_db), m2Models.size()));
                    Iterator<MigTwoModel> mI = m2Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                return toListView;
            } else {
                if(!getSf().isDatabaseCreated() || getSf().isDatabaseDeletedManually()) {
                    toListView.addLast(getString(R.string._l5_t2_m1_db_doesnt_exist));
                    return toListView;
                } else {
                    toListView.addLast(format(getString(R.string._l5_t2_m1_db_has_different_version), getSf().currentMigDBVersion()));
                    return toListView;
                }
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            int tableAmount = TABLE_AMOUNT;
            if(getSf().isDatabaseDeletedManually() || !getSf().isDatabaseCreated() || getSf().currentMigDBVersion() != DB_IMPLEMENTATION_VERSION)
                tableAmount = 0;
            if(result != null) {
                events.setAdapter(new MigAdapter(getContext(), result));
                int recordsAmount = result.size() - TABLE_AMOUNT;
                if(tableAmount == 0)
                    recordsAmount = 0;
                expandedTitle.setText(format(expandeddTitlePattern, recordsAmount, tableAmount));
            } else {
                events.setAdapter(new MigAdapter(getContext(), new LinkedList<String>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0, tableAmount));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson5tab2WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t2_running_requested_operation_pg_title),
                    getString(R.string._l5_t2_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                try {
                    KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    long recordsCount = mapper.countAll() + mapper2.countAll();
                    long affected = mapper.deleteAll() + mapper2.deleteAll();
                    mapper.close(); mapper2.close();
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, affected, recordsCount);
                } catch (Exception e) {
                    Log.e(MigrationDBv2.LTAG, ERR_STRING_WIPE, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv2.LTAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
                        }
                    }
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, -1l, -1l);
                }
            } else {
                return new WipeAsyncResult(
                        getSf().isDatabaseCreated(),
                        getSf().isDatabaseDeletedManually(),
                        getSf().currentMigDBVersion(),
                        -1l, -1l);
            }
        }

        @Override
        protected void onPostExecute(WipeAsyncResult result) {
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

                if(!result.isCreated) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_op_not_existing));
                } else if (result.isDeleted) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_op_deleted));
                } else if (result.dbVersion != DB_IMPLEMENTATION_VERSION) {
                    if(result.dbVersion < 1) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_lower), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    }
                } else if (result.recordsCount > -1 && result.affectedRows > -1) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t2_error_event));
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }
    }

    class WipeAsyncResult {
        boolean isCreated;
        boolean isDeleted;
        int dbVersion;
        Long affectedRows;
        Long recordsCount;

        public WipeAsyncResult(boolean isCreated, boolean isDeleted, int dbVersion,
                               Long affectedRows, Long recordsCount) {
            this.isCreated = isCreated;
            this.isDeleted = isDeleted;
            this.dbVersion = dbVersion;
            this.affectedRows = affectedRows;
            this.recordsCount = recordsCount;
        }
    }

    static final int INSERT_AMOUNT = 25;
    static final int INSERT_FK_AMOUNT = 10;

    static final String ERR_INSERT_RND = "Lesson5tab2InsertRNDDataError, see exception details!";

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t2_running_requested_operation_pg_title),
                    getString(R.string._l5_t2_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
            if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                return new InsertRandomResults(
                        null,
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
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    long recordsCount = mapper.countAll() + mapper2.countAll();
                    long affected = mapper.deleteAll() + mapper2.deleteAll();
                    LinkedList<MigOneModel> modelsToInsert = new LinkedList<>();
                    getSf().setDatabaseCreated(true);
                    getSf().setCurrentMigDBVersion(DB_IMPLEMENTATION_VERSION);
                    getSf().setDatabaseDeletedManually(false);
                    MigV2RandomModelFactory factory = new MigV2RandomModelFactory(getContext());
                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigOneModel m = factory.newM1RndModel();
                        modelsToInsert.addLast(m);
                    }
                    mapper.insertInTransaction(modelsToInsert);
                    List<MigOneModel> models = mapper.findAll();

                    Iterator<MigOneModel> mI = models.iterator();
                    LinkedList<String> out = new LinkedList<>();
                    while (mI.hasNext()) {
                        out.addLast(mI.next().toString());
                    }

                    LinkedList<MigTwoModel> models2ToInsert = new LinkedList<>();
                    for (int i = 0; i < INSERT_FK_AMOUNT; i++) {
                        MigTwoModel m = factory.newM2RndModel((ArrayList<MigOneModel>) models);
                        models2ToInsert.addLast(m);
                    }
                    mapper2.insertInTransaction(models2ToInsert);
                    List<MigTwoModel> models2 = mapper2.findAll();

                    LinkedList<String> out2 = new LinkedList<>();
                    Iterator<MigTwoModel> mI2 = models2.iterator();
                    while (mI2.hasNext()) {
                        out2.addLast(mI2.next().toString());
                    }

                    long recordsCountAfter = mapper.countAll() + mapper2.countAll();
                    mapper.close(); mapper2.close();
                    return new InsertRandomResults(out, out2, affected, recordsCount, recordsCountAfter, true, getSf().currentMigDBVersion());
                } catch (Exception e) {
                    Log.e(MigrationDBv1.LTAG, ERR_INSERT_RND, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv1.LTAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
                        }
                    }
                    return new InsertRandomResults(
                            null,
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_deleted_to_events), result.deletedModelsAffectedRows));
                    for (String modelString : result.modelInsertionsM1) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_inserted_to_events), M1M1TN, modelString));
                    }
                    for (String modelString2 : result.modelInsertionsM2) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_inserted_to_events), M1M2TN, modelString2));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_count_to_events), result.modelsCountAfter));
                } else {
                    if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t2_error_event));
                    }
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }


    }

    class InsertRandomResults {
        List<String> modelInsertionsM1;
        List<String> modelInsertionsM2;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;
        int dbVersion;

        public InsertRandomResults(List<String> modelInsertionsM1, List<String> modelInsertionsM2,
                                   long deletedModelsAffectedRows,
                                   long modelsCountBefore, long modelsCountAfter, boolean opSuccess,
                                   int dbVersion) {
            this.modelInsertionsM1 = modelInsertionsM1;
            this.modelInsertionsM2 = modelInsertionsM2;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
            this.dbVersion = dbVersion;
        }
    }

    static final String ERR_DELETION = "Lesson5tab2DBDeleteError, see exception details!";

    class DeleteDatabaseAsync extends AsyncTask<Long, Long, Integer> {
        ProgressDialog dialog;

        final int DELETED = 1;
        final int NOT_DELETED = 2;
        final int ERROR = 3;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t2_running_requested_operation_pg_title),
                    getString(R.string._l5_t2_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected Integer doInBackground(Long... strings) {
            try {
                boolean deleted = getDatabase().deleteDatabase();
                getSf().setDatabaseDeletedManually(true);
                getSf().setDatabaseCreated(false);
                getSf().setCurrentMigDBVersion(-1);
                if(deleted)
                    return DELETED;
                else
                    return NOT_DELETED;
            } catch (Exception e) {
                Log.e(MigrationDBv2.LTAG, ERR_DELETION, e);
                if (e instanceof KittyRuntimeException) {
                    if (((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(MigrationDBv2.LTAG, ERR_DELETION, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
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
                switch (result) {
                    case DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_delete_db_success)));
                        break;
                    case NOT_DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_delete_db_fail)));
                        break;
                    case ERROR:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t2_error_event)));
                        break;
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }
    }

    // Expanded list
    MigAdapter migAdapter;

    @Override
    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, 0));

        if(expandedAdapter == null) {
            migAdapter = new MigAdapter(getContext(), new LinkedList<String>());
        }

        events.setAdapter(migAdapter);
        events.setOnTouchListener(new View.OnTouchListener() {

            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T2_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T2_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T2_SCHEMA);
            }
        };
    }
}

{{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
