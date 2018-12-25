---
title: "Lesson5 tab 3 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**KittyORM `mig` v.3 implementation sources**

1. 
<details> 
  <summary>Click to view `MigrationDBv3.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        isLoggingOn = true,
        isProductionOn = false,
        isKittyDexUtilLoggingEnabled = false,
        logTag = MigrationDBv3.LTAG,
        databaseName = "mig",
        databaseVersion = 3,
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.migrations.migv3"}
)
@KITTY_DATABASE_REGISTRY(
        domainModels = {
                net.akaish.kittyormdemo.sqlite.migrations.migv3.MigOneModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv3.MigTwoModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv3.MigThreeModel.class
        }
)
@KITTY_DATABASE_HELPER(
        onUpgradeBehavior = KITTY_DATABASE_HELPER.UpgradeBehavior.USE_SIMPLE_MIGRATIONS
)
public class MigrationDBv3 extends KittyDatabase {

    public static final String LTAG = "MIGv3";
    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     *
     * @param ctx
     */
    public MigrationDBv3(Context ctx) {
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

    @KITTY_COLUMN(
            columnOrder = 1
    )
    @NOT_NULL
    @DEFAULT(predefinedLiteralValue = LiteralValues.CURRENT_DATE)
    public String creationDate;

    @KITTY_COLUMN(
            columnOrder = 2
    )
    @DEFAULT(signedInteger = 228)
    @ONE_COLUMN_INDEX(indexName = "m1_di_index")
    public Integer defaultInteger;

    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; creationDate = ")
                .append(creationDate)
                .append(" ; someInteger = ")
                .append(defaultInteger)
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

    @KITTY_COLUMN(columnOrder = 2)
    public Animals someAnimal;

    @KITTY_COLUMN(
            columnOrder = 3,
            columnAffinity = TypeAffinities.TEXT
    )
    @KITTY_COLUMN_SERIALIZATION
    public AnimalSounds someAnimalSound;

    String someAnimalSoundSerialize() {
        if(someAnimalSound == null) return null;
        return new GsonBuilder().create().toJson(someAnimalSound);
    }

    AnimalSounds someAnimalSoundDeserialize(String cvData) {
        if(cvData == null) return null;
        if(cvData.length() == 0) return null;
        return new GsonBuilder().create().fromJson(cvData, AnimalSounds.class);
    }

    @Override
    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; migOneReference = ")
                .append(migOneReference)
                .append(" ; someAnimal = ")
                .append(someAnimal)
                .append(" ; someAnimalSound = ")
                .append(someAnimalSoundSerialize())
                .append(" ] ").toString();
    }
}
{{< /highlight >}} 
</details>

4. 
<details> 
  <summary>Click to view `MigThreeModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "mig_three")
public class MigThreeModel extends KittyModel {

    @KITTY_COLUMN(
            columnOrder = 0,
            isIPK = true
    )
    public Long id;

    @KITTY_COLUMN(
            columnOrder = 1
    )
    @NOT_NULL
    @DEFAULT(
            literalValue = "\'Something random\'"
    )
    public String someValue;

    public String toString() {
        return new StringBuilder(32)
                .append("[ id = ")
                .append(id)
                .append(" ; someValue = ")
                .append(someValue)
                .append(" ]").toString();
    }
}
{{< /highlight >}} 
</details>

**Fragment and utility code used in this tutorial**

1. 
<details> 
  <summary>Click to view `MigV3RandomModelFactory.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class MigV3RandomModelFactory {
    final Context context;
    final Random rnd;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    public MigV3RandomModelFactory(Context ctx) {
        this.context = ctx;
        this.rnd = new Random();

        // Lol, getContext().getString() method is fucking slow, calling for each new random model this method twice causes 55% of all execution time of generating new random model (!)
        // Right now getting those string causes only 14% of execution time
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.BEAR), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.BEAR)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.CAT), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.CAT)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.DOG), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.DOG)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.GOAT), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.GOAT)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.LION), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.LION)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.SHEEP), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.SHEEP)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.TIGER), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.TIGER)));
        randomAnimalSays.append(Animals.getLocalizedAnimalSaysResource(Animals.WOLF), context.getString(Animals.getLocalizedAnimalSaysResource(Animals.WOLF)));


        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.BEAR), context.getString(Animals.getLocalizedAnimalNameResource(Animals.BEAR)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.CAT), context.getString(Animals.getLocalizedAnimalNameResource(Animals.CAT)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.DOG), context.getString(Animals.getLocalizedAnimalNameResource(Animals.DOG)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.GOAT), context.getString(Animals.getLocalizedAnimalNameResource(Animals.GOAT)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.LION), context.getString(Animals.getLocalizedAnimalNameResource(Animals.LION)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.SHEEP), context.getString(Animals.getLocalizedAnimalNameResource(Animals.SHEEP)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.TIGER), context.getString(Animals.getLocalizedAnimalNameResource(Animals.TIGER)));
        randomAnimalLocalizedName.append(Animals.getLocalizedAnimalNameResource(Animals.WOLF), context.getString(Animals.getLocalizedAnimalNameResource(Animals.WOLF)));
    }

    public MigOneModel newM1RndModel() {
        return newM1RndModel(rnd.nextBoolean(), rnd.nextBoolean());
    }

    public MigOneModel newM1RndModel(boolean setCDDefault, boolean setDefaultInteger) {
        MigOneModel model = new MigOneModel();
        if(setCDDefault)
            model.setFieldExclusion("creationDate");
        else
            model.creationDate = new Date(System.currentTimeMillis()).toString();
        if(setCDDefault)
            model.setFieldExclusion("defaultInteger");
        else
            model.defaultInteger = rnd.nextInt();
        return model;
    }

    public MigTwoModel newM2RndModel(ArrayList<MigOneModel> models) {
        if(models == null)
            throw new IllegalArgumentException("M3RMF#newM2RndModel bad model collection provided!");
        if(models.size() == 0)
            throw new IllegalArgumentException("M3RMF#newM2RndModel bad model collection provided!");
        int mlSize = models.size();
        return newM2RndModel(models.get(rnd.nextInt(mlSize)).id);
    }

    public MigTwoModel newM2RndModel(Long migOneReference) {
        if(migOneReference == null)
            throw new IllegalArgumentException("M3RMF#newM2RndModel bad reference id provided!");
        MigTwoModel model = new MigTwoModel();
        model.someAnimal = Animals.rndAnimal(rnd);
        model.migOneReference = migOneReference;
        AnimalSounds animalSounds = new AnimalSounds();
        animalSounds.animalName = randomAnimalLocalizedName.get(Animals.getLocalizedAnimalNameResource(model.someAnimal));
        animalSounds.animalSounds = randomAnimalSays.get(Animals.getLocalizedAnimalSaysResource(model.someAnimal));
        model.someAnimalSound = animalSounds;
        return model;
    }

    static final String[] M3_SOME_VALUES = {"One", "Apple", "Wolf", "Plane", "Name", "Fear of being alone", "Despair", "Death", "Do not look for meaning where it is not"};

    public MigThreeModel newM3RndModel() {
        return newM3RndModel(rnd.nextBoolean());
    }

    public MigThreeModel newM3RndModel(boolean setDefaultValue) {
        MigThreeModel model = new MigThreeModel();
        if(setDefaultValue)
            model.setFieldExclusion("someValue");
        else
            model.someValue = M3_SOME_VALUES[rnd.nextInt(M3_SOME_VALUES.length)];
        return model;
    }
}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `Lesson5Tab3AutogenMigration.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class Lesson5Tab3AutogenMigration extends Lesson5BaseFragment {

    private MigrationDBv3 databaseV3;
    private SharedPreferencesMigDB sf;

    private Button insertRandomButton;
    private Button clearTableButton;
    private Button deleteDatabaseButton;

    private ListView eventsListView;

    private TextView statusTV;

    private MigDatabaseState mdbState;

    final static int DB_IMPLEMENTATION_VERSION = 3;
    final static int TABLE_AMOUNT = 3;

    public Lesson5Tab3AutogenMigration() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson5_tab3_auto_migration, container, false);

        insertRandomButton = rootView.findViewById(R.id.l5_t3_go_button);
        clearTableButton = rootView.findViewById(R.id.l5_t3_clear_button);
        deleteDatabaseButton = rootView.findViewById(R.id.l5_t3_delete_database_button);

        eventsListView = rootView.findViewById(R.id.l5_t3_actions);

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

        statusTV = rootView.findViewById(R.id.l5_t3_status);


        setUpExpandedList(
                rootView,
                R.id._l5_t3_expanded_panel_list,
                R.id._l5_t3_expanded_panel_text,
                R.string._l5_t3_expanded_text_pattern
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
            statusTV.setText(getMdbState(getContext(), DB_IMPLEMENTATION_VERSION, new String[] {M1M1TN, M1M2TN, M1M3TN}).toString());
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

    private MigrationDBv3 getDatabase() {
        // retrieving existing database after upgrade -> downgrade would cause onUpgrade() script would be run after mapper fetching
        databaseV3 = new MigrationDBv3(getContext());
        return databaseV3;
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
        return R.string._l5_t3_snackbar_message;
    }



    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<String>> {

        @Override
        protected List<String> doInBackground(Long... params) {
            LinkedList<String> toListView = new LinkedList<>();
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                // T1
                KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                List<MigOneModel> m1Models = mapper.findAll();
                mapper.close();
                // T2
                KittyMapper mapperT2 = getDatabase().getMapper(MigTwoModel.class);
                List<MigTwoModel> m2Models = mapperT2.findAll();
                mapper.close();
                // T3
                KittyMapper mapperT3 = getDatabase().getMapper(MigThreeModel.class);
                List<MigThreeModel> m3Models = mapperT3.findAll();
                mapper.close();

                if(m1Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m1_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m1_db), m1Models.size()));
                    Iterator<MigOneModel> mI = m1Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m2Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m2_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m2_db), m2Models.size()));
                    Iterator<MigTwoModel> mI = m2Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m3Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m3_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t3_m3_db), m2Models.size()));
                    Iterator<MigThreeModel> mI = m3Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                return toListView;
            } else {
                if(!getSf().isDatabaseCreated() || getSf().isDatabaseDeletedManually()) {
                    toListView.addLast(getString(R.string._l5_t3_m1_db_doesnt_exist));
                    return toListView;
                } else {
                    toListView.addLast(format(getString(R.string._l5_t3_m1_db_has_different_version), getSf().currentMigDBVersion()));
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
                int recordsAmount = result.size() - tableAmount;
                if(tableAmount == 0)
                    recordsAmount = 0;
                expandedTitle.setText(format(expandeddTitlePattern, recordsAmount, tableAmount));
            } else {
                events.setAdapter(new MigAdapter(getContext(), new LinkedList<String>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0, tableAmount));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson5tab3WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t3_running_requested_operation_pg_title),
                    getString(R.string._l5_t3_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                try {
                    KittyMapper mapper = getDatabase().getMapper(MigOneModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper.countAll() + mapper2.countAll() + mapper3.countAll();
                    long affected = mapper.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    mapper.close(); mapper2.close(); mapper3.close();
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, affected, recordsCount);
                } catch (Exception e) {
                    Log.e(MigrationDBv3.LTAG, ERR_STRING_WIPE, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv3.LTAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t3_error_event));
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

    static final String ERR_INSERT_RND = "Lesson5tab3InsertRNDDataError, see exception details!";

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t3_running_requested_operation_pg_title),
                    getString(R.string._l5_t3_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
            if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                return new InsertRandomResults(
                        null,
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
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper.countAll() + mapper2.countAll() + mapper3.countAll();

                    boolean deleteData = getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION;

                    long affected;

                    if(deleteData)
                        affected = mapper.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    else
                        affected = 0l;

                    LinkedList<MigOneModel> modelsToInsert = new LinkedList<>();
                    LinkedList<MigTwoModel> models2ToInsert = new LinkedList<>();
                    LinkedList<MigThreeModel> models3ToInsert = new LinkedList<>();

                    getSf().setDatabaseCreated(true);
                    getSf().setCurrentMigDBVersion(DB_IMPLEMENTATION_VERSION);
                    getSf().setDatabaseDeletedManually(false);

                    MigV3RandomModelFactory factory = new MigV3RandomModelFactory(getContext());

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

                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigThreeModel m = factory.newM3RndModel();
                        models3ToInsert.addLast(m);
                    }
                    mapper3.insertInTransaction(models3ToInsert);
                    List<MigThreeModel> models3 = mapper3.findAll();
                    LinkedList<String> out3 = new LinkedList<>();
                    Iterator<MigThreeModel> mI3 = models3.iterator();
                    while (mI3.hasNext()) {
                        out3.addLast(mI3.next().toString());
                    }

                    long recordsCountAfter = mapper.countAll() + mapper2.countAll() + mapper3.countAll();
                    mapper.close(); mapper2.close(); mapper3.close();
                    return new InsertRandomResults(out, out2, out3, affected, recordsCount, recordsCountAfter, true, getSf().currentMigDBVersion());
                } catch (Exception e) {
                    Log.e(MigrationDBv3.LTAG, ERR_INSERT_RND, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv3.LTAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
                        }
                    }
                    return new InsertRandomResults(
                            null,
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_deleted_to_events), result.deletedModelsAffectedRows));
                    for (String modelString : result.modelInsertionsM1) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_inserted_to_events), M1M1TN, modelString));
                    }
                    for (String modelString2 : result.modelInsertionsM2) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_inserted_to_events), M1M2TN, modelString2));
                    }
                    for (String modelString3 : result.modelInsertionsM3) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_inserted_to_events), M1M3TN, modelString3));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_count_to_events), result.modelsCountAfter));
                } else {
                    if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t3_error_event));
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
        List<String> modelInsertionsM3;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;
        int dbVersion;

        public InsertRandomResults(List<String> modelInsertionsM1, List<String> modelInsertionsM2,
                                   List<String> modelInsertionsM3, long deletedModelsAffectedRows,
                                   long modelsCountBefore, long modelsCountAfter, boolean opSuccess,
                                   int dbVersion) {
            this.modelInsertionsM1 = modelInsertionsM1;
            this.modelInsertionsM2 = modelInsertionsM2;
            this.modelInsertionsM3 = modelInsertionsM3;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
            this.dbVersion = dbVersion;
        }
    }

    static final String ERR_DELETION = "Lesson5tab3DBDeleteError, see exception details!";

    class DeleteDatabaseAsync extends AsyncTask<Long, Long, Integer> {
        ProgressDialog dialog;

        final int DELETED = 1;
        final int NOT_DELETED = 2;
        final int ERROR = 3;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t3_running_requested_operation_pg_title),
                    getString(R.string._l5_t3_running_requested_operation_pg_body)
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
                Log.e(MigrationDBv3.LTAG, ERR_DELETION, e);
                if (e instanceof KittyRuntimeException) {
                    if (((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(MigrationDBv3.LTAG, ERR_DELETION, ((KittyRuntimeException) e).getNestedException());
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
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_delete_db_success)));
                        break;
                    case NOT_DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_delete_db_fail)));
                        break;
                    case ERROR:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t3_error_event)));
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T3_TUTORIAL);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T3_SOURCE);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T3_SCHEMA);
            }
        };
    }
}
{{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
