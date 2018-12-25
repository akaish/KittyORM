---
title: "Lesson3 tab 1 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**KittyORM custom mapping implementation example**

1. 
<details> 
  <summary>Click to view model field annotation example: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
// Saving to text
@KITTY_COLUMN(
        columnOrder = 18,
        columnAffinity = TypeAffinities.TEXT
)
@KITTY_COLUMN_SERIALIZATION
public AnimalSounds stringSDF;

// Saving to blob
@KITTY_COLUMN(
        columnOrder = 20,
        columnAffinity = TypeAffinities.BLOB
)
@KITTY_COLUMN_SERIALIZATION
public Bitmap byteArraySDF;
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to serialization\deserialization methods implementation examples: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
String stringSDFSerialize() {
    if(stringSDF == null) return null;
    return new GsonBuilder().create().toJson(stringSDF);
}

AnimalSounds stringSDFDeserialize(String cvData) {
    if(cvData == null) return null;
    if(cvData.length() == 0) return null;
    return new GsonBuilder().create().fromJson(cvData, AnimalSounds.class);
}

public byte[] byteArraySDFSerialize() {//byteArraySDFSerialize
    if(byteArraySDF == null) return null;
    ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
    byteArraySDF.compress(Bitmap.CompressFormat.PNG, 100, bmpStream);
    return bmpStream.toByteArray();
}

public Bitmap byteArraySDFDeserialize(byte[] cursorData) {
    if(cursorData == null) return null;
    if(cursorData.length == 0) return null;
    return BitmapFactory.decodeByteArray(cursorData, 0, cursorData.length);
}
{{< /highlight >}} 
</details>

**KittyORM `basic_datase` implementation sources**

1. 
<details> 
  <summary>Click to view `BasicDatabase.class`: </summary>
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
        domainPairs = {
                @KITTY_REGISTRY_PAIR(model = ComplexRandomModel.class, mapper = ComplexRandomMapper.class),
                @KITTY_REGISTRY_PAIR(model = IndexesAndConstraintsModel.class),
                @KITTY_REGISTRY_PAIR(model = RandomModel.class, mapper = RandomMapper.class)
        }
)
public class BasicDatabase extends KittyDatabase {

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

}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `AbstractRandomModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public abstract class AbstractRandomModel extends KittyModel {

    public static final String RND_INTEGER_CNAME = "rnd_int_custom_column_name";
    public static final String RND_ANIMAL_CNAME = "rndanimal";

    @KITTY_COLUMN(
            isIPK = true,
            columnOrder = 0
    )
    public Long id;

    @KITTY_COLUMN(
            columnOrder = 1
    )
    public int randomInt;

    @KITTY_COLUMN(
            columnOrder = 2,
            columnName = RND_INTEGER_CNAME
    )
    public Integer randomInteger;

    @KITTY_COLUMN(
            columnOrder = 3,
            columnName = RND_ANIMAL_CNAME
    )
    public Animals randomAnimal;

    @KITTY_COLUMN(
            columnOrder = 4,
            columnAffinity = TypeAffinities.TEXT
    )
    public String randomAnimalName;
}
{{< /highlight >}} 
</details>

3. 
<details> 
  <summary>Click to view `ComplexRandomModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE
@KITTY_EXTENDED_CRUD(extendedCrudController = ComplexRandomMapper.class)
public class ComplexRandomModel extends AbstractRandomModel {

    public ComplexRandomModel() {
        super();
    }


    // Primitives
    // (boolean, int, byte, double, long, short, float)
    @KITTY_COLUMN(columnOrder = 5)
    public boolean boolF;


    @KITTY_COLUMN(columnOrder = 6)
    public byte byteF;

    @KITTY_COLUMN(columnOrder = 7)
    public double doubleF;

    @KITTY_COLUMN(columnOrder = 8)
    public long longF;

    @KITTY_COLUMN(columnOrder = 9)
    public short shortF;

    @KITTY_COLUMN(columnOrder = 10)
    public float floatF;

    // Byte array
    @KITTY_COLUMN(columnOrder = 11)
    public byte[] byteArray;

    // String (TEXT) (String, BigDecimal, BigInteger, Enum)
    @KITTY_COLUMN(columnOrder = 12)
    public String stringF;

    @KITTY_COLUMN(columnOrder = 13)
    public BigDecimal bigDecimalF;

    @KITTY_COLUMN(columnOrder = 14)
    public BigInteger bigIntegerF;

    @KITTY_COLUMN(columnOrder = 15)
    public Uri uriF;

    @KITTY_COLUMN(columnOrder = 16)
    public File fileF;

    @KITTY_COLUMN(columnOrder = 17)
    public Currency currencyF;

    // SD
    @KITTY_COLUMN(
            columnOrder = 18,
            columnAffinity = TypeAffinities.TEXT
    )
    @KITTY_COLUMN_SERIALIZATION
    public AnimalSounds stringSDF;

    @KITTY_COLUMN(columnOrder = 19)
    public SomeColours bitmapColour;

    @KITTY_COLUMN(
            columnOrder = 20,
            columnAffinity = TypeAffinities.BLOB
    )
    @KITTY_COLUMN_SERIALIZATION
    public Bitmap byteArraySDF;

    String stringSDFSerialize() {
        if(stringSDF == null) return null;
        return new GsonBuilder().create().toJson(stringSDF);
    }

    AnimalSounds stringSDFDeserialize(String cvData) {
        if(cvData == null) return null;
        if(cvData.length() == 0) return null;
        return new GsonBuilder().create().fromJson(cvData, AnimalSounds.class);
    }

    public byte[] byteArraySDFSerialize() {//byteArraySDFSerialize
        if(byteArraySDF == null) return null;
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        byteArraySDF.compress(Bitmap.CompressFormat.PNG, 100, bmpStream);
        return bmpStream.toByteArray();
    }

    public Bitmap byteArraySDFDeserialize(byte[] cursorData) {
        if(cursorData == null) return null;
        if(cursorData.length == 0) return null;
        return BitmapFactory.decodeByteArray(cursorData, 0, cursorData.length);
    }

    // Primitive wrappers Boolean, Integer, Byte, Double, Short or Float
    @KITTY_COLUMN(columnOrder = 21)
    public Boolean boolFF;


    @KITTY_COLUMN(columnOrder = 22)
    public Byte byteFF;

    @KITTY_COLUMN(columnOrder = 23)
    public Double doubleFF;

    @KITTY_COLUMN(columnOrder = 24)
    public Short shortFF;

    @KITTY_COLUMN(columnOrder = 25)
    public Float floatFF;


    // Long represented types Long, Date, Calendar, Timestamp
    @KITTY_COLUMN(columnOrder = 26)
    public Long longFF;

    @KITTY_COLUMN(columnOrder = 27)
    public Date dateF;

    @KITTY_COLUMN(columnOrder = 28)
    public Calendar calendarF;

    @KITTY_COLUMN(columnOrder = 29)
    public Timestamp timestampF;

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer(256);
        out.append("Long id : "+id+"\r\n");
        out.append("int randomInt : "+randomInt+"\r\n");
        out.append("String stringF : "+stringF+"\r\n");
        out.append("BigInteger bigIntegerF : "+bigIntegerF+"\r\n");
        out.append("SomeColours bitmapColour : "+bitmapColour+"\r\n");
        out.append("Short shortFF : "+shortFF+"\r\n");
        out.append("Timestamp timestampF (HReadable) : "+timestampF+"\r\n");
        out.append("AnimalSounds stringSDF (HReadable) : "+stringSDFSerialize()+"\r\n");
        out.append("Uri uriF : " + uriF+"\r\n");
        out.append("Currency currencyF : " + currencyF.getSymbol()+"\r\n");
        out.append("... \r\n");
        return out.toString();
    }

    public String toShortString() {
        StringBuffer out = new StringBuffer(256);
        out.append("[ Long id : "+id+"; ");
        out.append("int randomInt : "+randomInt+"; ");
        out.append("String stringF : "+stringF+"; ");
        out.append("BigInteger bigIntegerF : "+bigIntegerF+"; ");
        out.append("SomeColours bitmapColour : "+bitmapColour+"; ");
        out.append("Short shortFF : "+shortFF+"; ");
        out.append("Timestamp timestampF (HReadable) : "+timestampF+"; ... ]");
        return out.toString();
    }

    @Deprecated
    public String toHTMLString() {
        StringBuffer out = new StringBuffer(2048);
        out.append("<br>Long id : "+id.toString()+"\r\n");
        out.append("<br><b>PRIMITIVES</b>"+"\r\n");
        out.append("<br>boolean boolF : "+Boolean.toString(boolF)+"\r\n");
        out.append("<br>int randomInt : "+Integer.toString(randomInt)+"\r\n");
        out.append("<br>byte byteF : "+Byte.toString(byteF)+"\r\n");
        out.append("<br>double doubleF : "+Double.toString(doubleF)+"\r\n");
        out.append("<br>long longF : "+Long.toString(longF)+"\r\n");
        out.append("<br>short shortF : "+Short.toString(shortF)+"\r\n");
        out.append("<br>float floatF : "+Float.toString(floatF)+"\r\n");
        out.append("<br>byte[] byteArray : "+byteArrayToString(byteArray)+"\r\n");
        out.append("<br><b>STRING AFFINITIES</b>"+"\r\n");
        out.append("<br>String randomAnimalName : "+randomAnimalName+"\r\n");
        out.append("<br>String stringF : "+stringF+"\r\n");
        out.append("<br>BigDecimal bigDecimalF : "+bigDecimalF.toEngineeringString()+"\r\n");
        out.append("<br>BigInteger bigIntegerF : "+bigIntegerF.toString()+"\r\n");
        out.append("<br>Animals randomAnimal : "+randomAnimal.toString()+"\r\n");
        out.append("<br><b>SERIALIZATION AND DESERIALIZATION</b>"+"\r\n");
        out.append("<br>AnimalSounds stringSDF : "+stringSDFSerialize()+"\r\n");
        out.append("<br>SomeColours bitmapColour : "+bitmapColour.toString()+"\r\n");
        out.append("<br><b>PRIMITIVE WRAPPERS</b>"+"\r\n");
        out.append("<br>Boolean boolFF : "+boolFF.toString()+"\r\n");
        out.append("<br>Integer randomInteger : "+randomInteger.toString()+"\r\n");
        out.append("<br>Byte byteFF : "+byteFF.toString()+"\r\n");
        out.append("<br>Double doubleFF : "+doubleFF.toString()+"\r\n");
        out.append("<br>Short shortFF : "+shortFF.toString()+"\r\n");
        out.append("<br>Float floatFF :"+floatFF.toString()+"\r\n");
        out.append("<br><b>LONG REPRESENTED TYPES</b>"+"\r\n");
        out.append("<br>Long longFF : "+longFF.toString()+"\r\n");
        out.append("<br>Date dateF : "+Long.toString(dateF.getTime())+"\r\n");
        out.append("<br>Calendar calendarF : "+Long.toString(calendarF.getTimeInMillis())+"\r\n");
        out.append("<br>Timestamp timestampF : "+Long.toString(timestampF.getTime())+"\r\n");
        out.append("<br>Date dateF (HReadable) : "+dateF.toString()+"\r\n");
        out.append("<br>Calendar calendarF (HReadable) : "+calendarF.getTime().toString()+"\r\n");
        out.append("<br>Timestamp timestampF (HReadable) : "+timestampF.toString()+"\r\n");
        return out.toString();
    }

    public String byteArrayToString(byte[] toString) {
        String[] strings = new String[toString.length];
        for(int i = 0; i < toString.length; i++) {
            strings[i] = Byte.toString(toString[i]);
        }
        return KittyUtils.implodeWithCommaInBKT(strings);
    }
}
{{< /highlight >}} 
</details>

4. 
<details> 
  <summary>Click to view `ComplexRandomMapper.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class ComplexRandomMapper extends KittyMapper {

    public <M extends KittyModel> ComplexRandomMapper(KittyTableConfiguration tableConfiguration, M blankModelInstance, String databasePassword) {
        super(tableConfiguration, blankModelInstance, databasePassword);
    }

}
{{< /highlight >}} 
</details>

**Fragment and utility code used in this tutorial**

1. 
<details> 
  <summary>Click to view `RNDComplexRandomModelFactory.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class RNDComplexRandomModelFactory {

    private final Random randomizer;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    private final Context context;

    private final static String[] ISO4217_EXAMPLE_CODES = {"USD", "GBP", "EUR", "CNY", "JPY", "MYR", "AUD", "HKD", "PHP"}; // "KOR" currency code not present at 4.4
    private final static String[] URI_EXAMPLES = {"http://example.com", "file:///usr/somefile", "http://example.org"};
    private final static String[] FILE_EXAPLES = {"/file/one", "/file/two/some.txt", "/file/three/sys.iso"};

    public RNDComplexRandomModelFactory(Context context) {
        super();
        this.context = context;

        this.randomizer = new Random();

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

    public ComplexRandomModel newComplexRandomModel() {
        ComplexRandomModel model = new ComplexRandomModel();

        model.boolF = randomizer.nextBoolean();
        model.randomInt = randomizer.nextInt();
        model.byteF = (byte) randomizer.nextInt(128);
        model.doubleF = randomizer.nextDouble();
        model.longF = randomizer.nextLong();
        model.shortF = (short) randomizer.nextInt(1000);
        model.floatF = randomizer.nextFloat();
        model.byteArray = new byte[8];
        randomizer.nextBytes(model.byteArray);

        StringBuffer rndStrBuffer = new StringBuffer(8);
        for(int i = 0; i < model.byteArray.length; i++) {
            rndStrBuffer.append((char) model.byteArray[i]);
        }
        model.stringF = rndStrBuffer.toString();
        model.bigDecimalF = new BigDecimal(randomizer.nextDouble());
        model.bigIntegerF = BigInteger.valueOf(randomizer.nextLong());
        model.randomAnimal = Animals.rndAnimal(randomizer);
        model.uriF = Uri.parse(URI_EXAMPLES[randomizer.nextInt(URI_EXAMPLES.length)]);
        model.fileF = new File(FILE_EXAPLES[randomizer.nextInt(FILE_EXAPLES.length)]);
        model.currencyF = Currency.getInstance(ISO4217_EXAMPLE_CODES[randomizer.nextInt(ISO4217_EXAMPLE_CODES.length)]);

        AnimalSounds animalSounds = new AnimalSounds();
        animalSounds.animalName = randomAnimalLocalizedName.get(Animals.getLocalizedAnimalNameResource(model.randomAnimal));
        animalSounds.animalSounds = randomAnimalSays.get(Animals.getLocalizedAnimalSaysResource(model.randomAnimal));
        model.stringSDF = animalSounds;

        model.bitmapColour = SomeColours.rndColour(randomizer);
        model.byteArraySDF = SomeColours.getSomeColourBitmpap(
                SomeColours.getSomeColoursBitmapResource(model.bitmapColour), context
        );

        model.boolFF = Boolean.valueOf(randomizer.nextBoolean());
        model.randomInteger = Integer.valueOf(randomizer.nextInt());
        model.randomAnimalName = context.getString(Animals.getLocalizedAnimalNameResource(model.randomAnimal));
        model.byteFF = Byte.valueOf((byte) randomizer.nextInt(128));
        model.doubleFF = Double.valueOf(randomizer.nextDouble());
        model.shortFF = new Short((short) randomizer.nextInt(10000));
        model.floatFF = Float.valueOf(randomizer.nextFloat());

        model.longFF = currentTimeMillis();
        model.dateF = new Date(model.longFF);
        model.calendarF = Calendar.getInstance();
        model.calendarF.setTimeInMillis(model.longFF);
        model.timestampF = new Timestamp(model.longFF);

        return model;
    }
}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `Lesson3Tab1DatatypesAffinities.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class Lesson3Tab1DatatypesAffinities extends Lesson3BaseFragment {

    private BasicDatabase database;

    public Lesson3Tab1DatatypesAffinities() {}

    private Button insertRandomButton;
    private Button clearTableButton;
    private ListView eventsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson3_tab1_datatype_affinities, container, false);

        insertRandomButton = rootView.findViewById(R.id.l3_t1_go_button);
        clearTableButton = rootView.findViewById(R.id.l3_t1_clear_button);

        eventsListView = rootView.findViewById(R.id.l3_t1_actions);

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

        setUpExpandedList(
                rootView,
                R.id._l3_t1_expanded_panel_list,
                R.id._l3_t1_expanded_panel_text,
                R.string._l3_t1_expanded_text_pattern
        );

        reloadTableExpandedList();
        return rootView;
    }

    private BasicDatabase getDatabase() {
        if(database != null) return database;
        database = new BasicDatabase(getContext());
        return database;
    }

    ComplexRandomModelDTAAdapter complexExpandedAdapter;

    @Override
    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, 0));

        if(expandedAdapter == null) {
            complexExpandedAdapter = new ComplexRandomModelDTAAdapter(getContext(), new LinkedList<ComplexRandomModel>());
        }

        events.setAdapter(complexExpandedAdapter);
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

    @Override
    public void onVisible() {
        reloadTableExpandedList();
    }

    private void insert25RND() {
        new InsertRandomAsync().execute(0l);
    }

    private void clearTable() {
        new WipeAsync().execute(0l);
    }

    private void reloadTableExpandedList() {
        new ReloadTableAsync().execute(0l);
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l3_t1_snackbar_message;
    }


    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<ComplexRandomModel>> {

        @Override
        protected List<ComplexRandomModel> doInBackground(Long... params) {
            ComplexRandomMapper mapper = (ComplexRandomMapper) Lesson3Tab1DatatypesAffinities.this.getDatabase().getMapper(ComplexRandomModel.class);
            List<ComplexRandomModel> out = mapper.findAll();
            mapper.close();
            return out;
        }

        @Override
        protected void onPostExecute(List<ComplexRandomModel> result) {
            if(result != null) {
                events.setAdapter(new ComplexRandomModelDTAAdapter(getContext(), (ArrayList<ComplexRandomModel>) result));
                expandedTitle.setText(format(expandeddTitlePattern, result.size()));
            } else {
                events.setAdapter(new ComplexRandomModelDTAAdapter(getContext(), new LinkedList<ComplexRandomModel>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson3tab1WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, WipeAsyncResult> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson3Tab1DatatypesAffinities.this.getLessonActivity(),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_title),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected WipeAsyncResult doInBackground(Long... params) {
            try {
                final ComplexRandomMapper mapper = (ComplexRandomMapper) Lesson3Tab1DatatypesAffinities.this.getDatabase().getMapper(ComplexRandomModel.class);
                long recordsCount = mapper.countAll();
                long affected = mapper.deleteAll();
                mapper.close();
                return new WipeAsyncResult(affected, recordsCount);
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return new WipeAsyncResult(-1l, -1l);
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
                if (result.recordsCount > -1 && result.affectedRows > -1) {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_count_to_events), result.recordsCount));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_deleted_to_events), result.affectedRows));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l3_t1_error_event));
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
                reloadTableExpandedList();
            }
        }
    }

    class WipeAsyncResult {
        Long affectedRows;
        Long recordsCount;

        public WipeAsyncResult(Long affectedRows, Long recordsCount) {
            this.affectedRows = affectedRows;
            this.recordsCount = recordsCount;
        }
    }

    static final int INSERT_AMOUNT = 25;

    static final String ERR_INSERT_RND = "Lesson3tab1InsertRNDDataError, see exception details!";

    class InsertRandomAsync extends AsyncTask<Long, Long, InsertRandomResults> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson3Tab1DatatypesAffinities.this.getLessonActivity(),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_title),
                    Lesson3Tab1DatatypesAffinities.this.getString(R.string._l3_t1_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected InsertRandomResults doInBackground(Long... strings) {
            try {
                ComplexRandomMapper mapper = (ComplexRandomMapper) Lesson3Tab1DatatypesAffinities.this.getDatabase().getMapper(ComplexRandomModel.class);
                long recordsCount = mapper.countAll();
                long affected = mapper.deleteAll();
                LinkedList<ComplexRandomModel> modelsToInsert = new LinkedList<>();
                RNDComplexRandomModelFactory factory = new RNDComplexRandomModelFactory(getContext());
                for(int i = 0; i < INSERT_AMOUNT; i++) {
                    ComplexRandomModel m = factory.newComplexRandomModel();
                    modelsToInsert.addLast(m);
                }
                mapper.insertInTransaction(modelsToInsert);
                List<ComplexRandomModel> models = mapper.findAll();
                long recordsCountAfter = mapper.countAll();
                mapper.close();
                return new InsertRandomResults(models, affected, recordsCount, recordsCountAfter, true);
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_INSERT_RND, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_INSERT_RND, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return new InsertRandomResults(null, -1l, -1l, -1l, false);
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
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_count_to_events), result.modelsCountBefore));
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_deleted_to_events), result.deletedModelsAffectedRows));
                    for(ComplexRandomModel m : result.modelInsertions) {
                        ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_inserted_to_events), m.toShortString()));
                    }
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(format(getString(R.string._l3_t1_count_to_events), result.modelsCountAfter));
                } else {
                    ((BasicArrayAdapter) eventsListView.getAdapter()).addItemLast(getString(R.string._l3_t1_error_event));
                }
                ((BasicArrayAdapter) eventsListView.getAdapter()).notifyDataSetChanged();
            }
            reloadTableExpandedList();
        }


    }

    class InsertRandomResults {
        List<ComplexRandomModel> modelInsertions;
        long deletedModelsAffectedRows;
        long modelsCountBefore;
        long modelsCountAfter;
        boolean operationSuccess;

        public InsertRandomResults(List<ComplexRandomModel> modelInsertions, long deletedModelsAffectedRows, long modelsCountBefore, long modelsCountAfter, boolean opSuccess) {
            this.modelInsertions = modelInsertions;
            this.deletedModelsAffectedRows = deletedModelsAffectedRows;
            this.modelsCountBefore = modelsCountBefore;
            this.modelsCountAfter = modelsCountAfter;
            this.operationSuccess = opSuccess;
        }
    }

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T1_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T1_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T1_SCHEMA);
            }
        };
    }
}
{{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
