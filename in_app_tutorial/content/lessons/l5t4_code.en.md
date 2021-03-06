---
title: "Lesson5 tab 4 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**KittyORM `mig` v.4 implementation sources**

1. 
<details> 
  <summary>Click to view `MigrationDBv4.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_DATABASE(
        isLoggingOn = false,
        isProductionOn = true,
        isKittyDexUtilLoggingEnabled = false,
        databaseName = "mig",
        domainPackageNames = {"net.akaish.kittyormdemo.sqlite.migrations.migv4"},
        databaseVersion = 4,
        logTag = MigrationDBv4.LTAG
)
@KITTY_DATABASE_REGISTRY(
        domainModels = {
                net.akaish.kittyormdemo.sqlite.migrations.migv4.MigTwoModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv4.MigThreeModel.class,
                net.akaish.kittyormdemo.sqlite.migrations.migv4.MigFourModel.class
        }
)
@KITTY_DATABASE_HELPER(
        onUpgradeBehavior = KITTY_DATABASE_HELPER.UpgradeBehavior.USE_FILE_MIGRATIONS
)
public class MigrationDBv4 extends KittyDatabase {

    public static final String LTAG = "MIGv4";

    /**
     * KittyORM main database class that represents bootstrap and holder for all related with database
     * components.
     *
     * @param ctx
     */
    public MigrationDBv4(Context ctx) {
        super(ctx);
    }
}
{{< /highlight >}} 
</details>

2. 
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

3. 
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
            columnOrder = 1,
            columnName = "new_sv_name"
    )
    @NOT_NULL
    @DEFAULT(
            literalValue = "'Something random'"
    )
    public String someValue;

    @KITTY_COLUMN(columnOrder = 2)
    @DEFAULT(signedInteger = 22)
    @ONE_COLUMN_INDEX(indexName = "m3_rnd_long")
    public Long randomLong;

    public String toString() {
        return new StringBuilder(32)
                .append("[ id = ")
                .append(id)
                .append(" ; someValue = ")
                .append(someValue)
                .append(" ; randomLong = ")
                .append(randomLong)
                .append(" ]").toString();
    }
}
{{< /highlight >}} 
</details>

4. 
<details> 
  <summary>Click to view `MigFourModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "mig_four")
public class MigFourModel extends KittyModel {

    @KITTY_COLUMN(
            columnOrder = 0,
            isIPK = true)
    public Long id;

    @KITTY_COLUMN(columnOrder = 1)
    @FOREIGN_KEY(
            reference = @FOREIGN_KEY_REFERENCE(
                    foreignTableName = "mig_three",
                    foreignTableColumns = {"id"}
            )
    )
    @NOT_NULL
    public Long migThreeReference;

    @KITTY_COLUMN(columnOrder = 2)
    @FOREIGN_KEY(
            reference = @FOREIGN_KEY_REFERENCE(
                    foreignTableName = "mig_two",
                    foreignTableColumns = {"id"}
            )
    )
    @NOT_NULL
    public Long migTwoReference;

    @KITTY_COLUMN(columnOrder = 3)
    @NOT_NULL
    @DEFAULT(
            predefinedLiteralValue = LiteralValues.CURRENT_DATE
    )
    public Date creationDate;

    @Override
    public String toString() {
        return new StringBuilder(64)
                .append("[ id = ")
                .append(id)
                .append(" ; migThreeReferecnde = ")
                .append(migThreeReference)
                .append(" ; migTwoReference = ")
                .append(migTwoReference)
                .append(" ; creationDate = ")
                .append(creationDate)
                .append(" ]").toString();
    }
}
{{< /highlight >}} 
</details>

**Fragment and utility code used in this tutorial**

1. 
<details> 
  <summary>Click to view `MigV4RandomModelFactory.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class MigV4RandomModelFactory {

    final Context context;
    final Random rnd;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    public MigV4RandomModelFactory(Context ctx) {
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

    public MigTwoModel newM2RndModel() {
        MigTwoModel model = new MigTwoModel();
        model.someAnimal = Animals.rndAnimal(rnd);
        model.migOneReference = rnd.nextLong();
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
        model.randomLong = rnd.nextLong();
        return model;
    }

    public MigFourModel newM4RndModel(List<MigThreeModel> mig3Models, List<MigTwoModel> mig2Models) {
        if(mig2Models == null || mig3Models == null) {
            throw new IllegalArgumentException("M4RMF#newM4RndModel bad collections provided!");
        }
        if(mig2Models.size() == 0 || mig3Models.size() == 0) {
            throw new IllegalArgumentException("M4RMF#newM4RndModel bad collections provided!");
        }
        return newM4RndModel(
                rnd.nextBoolean(),
                mig2Models.get(rnd.nextInt(mig2Models.size())).id,
                mig3Models.get(rnd.nextInt(mig3Models.size())).id
        );
    }

    public MigFourModel newM4RndModel(boolean setDefaultValue, Long mig2reference, Long mig3reference) {
        if(mig2reference == null || mig3reference == null)
            throw new IllegalArgumentException("M4RMF#newM4RndModel bad references id provided!");
        MigFourModel model = new MigFourModel();
        model.migThreeReference = mig3reference;
        model.migTwoReference = mig2reference;
        if(setDefaultValue)
            model.setFieldExclusion("creationDate");
        else
            model.creationDate = new Date(System.currentTimeMillis());
        return model;
    }
}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `Lesson5Tab4FilescriptMigration.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class Lesson5Tab4FilescriptMigration extends Lesson5BaseFragment {

    private MigrationDBv4 databaseV4;
    private SharedPreferencesMigDB sf;

    private Button insertRandomButton;
    private Button clearTableButton;
    private Button deleteDatabaseButton;

    private ListView eventsListView;

    private TextView statusTV;

    private MigDatabaseState mdbState;

    final static int DB_IMPLEMENTATION_VERSION = 4;
    final static int TABLE_AMOUNT = 3;

    public Lesson5Tab4FilescriptMigration() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson5_tab4_file_migrations, container, false);

        insertRandomButton = rootView.findViewById(R.id.l5_t4_go_button);
        clearTableButton = rootView.findViewById(R.id.l5_t4_clear_button);
        deleteDatabaseButton = rootView.findViewById(R.id.l5_t4_delete_database_button);

        eventsListView = rootView.findViewById(R.id.l5_t4_actions);

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

        statusTV = rootView.findViewById(R.id.l5_t4_status);


        setUpExpandedList(
                rootView,
                R.id._l5_t4_expanded_panel_list,
                R.id._l5_t4_expanded_panel_text,
                R.string._l5_t4_expanded_text_pattern
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
            statusTV.setText(getMdbState(getContext(), DB_IMPLEMENTATION_VERSION, new String[] {M1M2TN, M1M3TN, M1M4TN}).toString());
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

    private MigrationDBv4 getDatabase() {
        // retrieving existing database after upgrade -> downgrade would cause onUpgrade() script would be run after mapper fetching
        databaseV4 = new MigrationDBv4(getContext());
        return databaseV4;
    }

    private void insert25RND() {
        new InsertRandomAsync().execute(0l);
        //new TestCPK().execute();
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
        return R.string._l5_t4_snackbar_message;
    }



    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<String>> {

        @Override
        protected List<String> doInBackground(Long... params) {
            LinkedList<String> toListView = new LinkedList<>();
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                // T4
                KittyMapper mapper = getDatabase().getMapper(MigFourModel.class);
                List<MigFourModel> m1Models = mapper.findAll();
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
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m4_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m4_db), m1Models.size()));
                    Iterator<MigFourModel> mI = m1Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m2Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m2_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m2_db), m2Models.size()));
                    Iterator<MigTwoModel> mI = m2Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                if(m3Models == null) {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m3_db), 0));
                } else {
                    toListView.addLast(format(getContext().getString(R.string._l5_t4_m3_db), m2Models.size()));
                    Iterator<MigThreeModel> mI = m3Models.iterator();
                    while (mI.hasNext()) {
                        toListView.addLast(mI.next().toString());
                    }
                }
                return toListView;
            } else {
                if(!getSf().isDatabaseCreated() || getSf().isDatabaseDeletedManually()) {
                    toListView.addLast(getString(R.string._l5_t4_m1_db_doesnt_exist));
                    return toListView;
                } else {
                    toListView.addLast(format(getString(R.string._l5_t4_m1_db_has_different_version), getSf().currentMigDBVersion()));
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

    private static final String ERR_STRING_WIPE = "Lesson5tab4WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t4_running_requested_operation_pg_title),
                    getString(R.string._l5_t4_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            if(getSf().isDatabaseCreated() && !getSf().isDatabaseDeletedManually() && getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION) {
                try {
                    KittyMapper mapper4 = getDatabase().getMapper(MigFourModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper4.countAll() + mapper2.countAll() + mapper3.countAll();
                    long affected = mapper4.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    mapper4.close(); mapper2.close(); mapper3.close();
                    return new WipeAsyncResult(true, false, DB_IMPLEMENTATION_VERSION, affected, recordsCount);
                } catch (Exception e) {
                    Log.e(MigrationDBv4.LTAG, ERR_STRING_WIPE, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv4.LTAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t4_error_event));
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

    static final String ERR_INSERT_RND = "Lesson5tab4InsertRNDDataError, see exception details!";

    // TEST COMPLEX PK
    class TestCPK extends AsyncTask<Void, Void, Long> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Long doInBackground(Void... voids) {
            KittyMapper mapper = getDatabase().getMapper(MigFiveModel.class);
            MigFiveModel m1 = new MigFiveModel();
            MigFiveModel m2 = new MigFiveModel();
            MigFiveModel m3 = new MigFiveModel();

            m1.ipkUniqueString = UUID.randomUUID().toString();
            m2.ipkUniqueString = UUID.randomUUID().toString();
            m3.ipkUniqueString = UUID.randomUUID().toString();

            m1.someStr = "STR1";
            m2.someStr = "STR2";
            m3.someStr = "STR3";

            mapper.save(m1);
            mapper.save(m2);
            mapper.save(m3);

            Log.e("CPK test 0", " count = "+mapper.countAll());

            SQLiteCondition sqLiteCondition = new SQLiteConditionBuilder()
                                                .addField("ipk_str")
                                                .addSQLOperator(SQLiteOperator.EQUAL)
                                                .addValue(m3.ipkUniqueString)
                                                .build();
            MigFiveModel m3FromDB = (MigFiveModel) mapper.findWhere(sqLiteCondition).get(0);
            Log.e("CPK test 1", m3FromDB.toString());
            m3FromDB.someStr = "modified";

            mapper.save(m3FromDB);

            SQLiteCondition sqLiteCondition2 = new SQLiteConditionBuilder()
                                                    .addField("some_str")
                                                    .addSQLOperator(SQLiteOperator.EQUAL)
                                                    .addValue("modified")
                                                    .build();
            MigFiveModel m3FromDBM = (MigFiveModel) mapper.findWhere(sqLiteCondition2).get(0);
            Log.e("CPK test 2", m3FromDB.toString());

            Log.e("CPK test 3", " count = "+mapper.countAll());

            mapper.deleteAll();
            return null;
        }
    }

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t4_running_requested_operation_pg_title),
                    getString(R.string._l5_t4_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
//            File destinationFile = getContext().getFilesDir();
//            KittyUtils.copyDirectoryFromAssetsToFS(getContext(), KittyNamingUtils.ASSETS_URI_START + "kittysqliteorm", destinationFile);
//            if(true) return null;
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
                    KittyMapper mapper4 = getDatabase().getMapper(MigFourModel.class);
                    KittyMapper mapper2 = getDatabase().getMapper(MigTwoModel.class);
                    KittyMapper mapper3 = getDatabase().getMapper(MigThreeModel.class);
                    long recordsCount = mapper4.countAll() + mapper2.countAll() + mapper3.countAll();

                    boolean deleteData = getSf().currentMigDBVersion() == DB_IMPLEMENTATION_VERSION;

                    long affected;

                    if(deleteData)
                        affected = mapper4.deleteAll() + mapper2.deleteAll() + mapper3.deleteAll();
                    else
                        affected = 0l;

                    LinkedList<MigFourModel> modelsToInsert = new LinkedList<>();
                    LinkedList<MigTwoModel> models2ToInsert = new LinkedList<>();
                    LinkedList<MigThreeModel> models3ToInsert = new LinkedList<>();

                    getSf().setDatabaseCreated(true);
                    getSf().setCurrentMigDBVersion(DB_IMPLEMENTATION_VERSION);
                    getSf().setDatabaseDeletedManually(false);

                    MigV4RandomModelFactory factory = new MigV4RandomModelFactory(getContext());

                    for (int i = 0; i < INSERT_AMOUNT; i++) {
                        MigTwoModel m = factory.newM2RndModel();
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

                    for (int i = 0; i < INSERT_FK_AMOUNT; i++) {
                        MigFourModel m = factory.newM4RndModel(models3, models2);
                        modelsToInsert.addLast(m);
                    }
                    mapper4.insertInTransaction(modelsToInsert);
                    List<MigFourModel> models = mapper4.findAll();
                    Iterator<MigFourModel> mI = models.iterator();
                    LinkedList<String> out4 = new LinkedList<>();
                    while (mI.hasNext()) {
                        out4.addLast(mI.next().toString());
                    }

                    long recordsCountAfter = mapper4.countAll() + mapper2.countAll() + mapper3.countAll();
                    mapper4.close(); mapper2.close(); mapper3.close();
                    return new InsertRandomResults(out4, out2, out3, affected, recordsCount, recordsCountAfter, true, getSf().currentMigDBVersion());
                } catch (Exception e) {
                    Log.e(MigrationDBv4.LTAG, ERR_INSERT_RND, e);
                    if (e instanceof KittyRuntimeException) {
                        if (((KittyRuntimeException) e).getNestedException() != null) {
                            Log.e(MigrationDBv4.LTAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_deleted_to_events), result.deletedModelsAffectedRows));
                    for (String modelString : result.modelInsertionsM4) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_inserted_to_events), M1M4TN, modelString));
                    }
                    for (String modelString2 : result.modelInsertionsM2) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_inserted_to_events), M1M2TN, modelString2));
                    }
                    for (String modelString3 : result.modelInsertionsM3) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_inserted_to_events), M1M3TN, modelString3));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_count_to_events), result.modelsCountAfter));
                } else {
                    if(getSf().currentMigDBVersion() > DB_IMPLEMENTATION_VERSION) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_op_mig_version_is_higher), result.dbVersion, DB_IMPLEMENTATION_VERSION));
                    } else {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l5_t4_error_event));
                    }
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
                reloadStatus();
            }
        }


    }

    class InsertRandomResults {
        List<String> modelInsertionsM4;
        List<String> modelInsertionsM2;
        List<String> modelInsertionsM3;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;
        int dbVersion;

        public InsertRandomResults(List<String> modelInsertionsM4, List<String> modelInsertionsM2,
                                   List<String> modelInsertionsM3, long deletedModelsAffectedRows,
                                   long modelsCountBefore, long modelsCountAfter, boolean opSuccess,
                                   int dbVersion) {
            this.modelInsertionsM4 = modelInsertionsM4;
            this.modelInsertionsM2 = modelInsertionsM2;
            this.modelInsertionsM3 = modelInsertionsM3;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
            this.dbVersion = dbVersion;
        }
    }

    static final String ERR_DELETION = "Lesson5tab4DBDeleteError, see exception details!";

    class DeleteDatabaseAsync extends AsyncTask<Long, Long, Integer> {
        ProgressDialog dialog;

        final int DELETED = 1;
        final int NOT_DELETED = 2;
        final int ERROR = 3;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    getLessonActivity(),
                    getString(R.string._l5_t4_running_requested_operation_pg_title),
                    getString(R.string._l5_t4_running_requested_operation_pg_body)
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
                Log.e(MigrationDBv4.LTAG, ERR_DELETION, e);
                if (e instanceof KittyRuntimeException) {
                    if (((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(MigrationDBv4.LTAG, ERR_DELETION, ((KittyRuntimeException) e).getNestedException());
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
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_delete_db_success)));
                        break;
                    case NOT_DELETED:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_delete_db_fail)));
                        break;
                    case ERROR:
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l5_t4_error_event)));
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T4_TUTORIAL);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T4_SOURCE);
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
                ((KittyTutorialActivity) getParentFragment().getActivity()).showWebViewDialog(L5_T4_SCHEMA);
            }
        };
    }
}
{{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
