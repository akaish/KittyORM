---
title: "Lesson3 tab 2 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**KittyORM constraint declaration examples**

1. 
<details> 
  <summary>Click to view `NOT NULL` constraint declaration example: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 0)
@PRIMARY_KEY
@NOT_NULL // NOT NULL constraint declaration
public Long id;
{{< /highlight >}}
</details>

2. 
<details> 
  <summary>Click to view `DEFAULT` constraint declaration examples: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 3)
@DEFAULT(signedInteger = 28) // You can choose for options for default declaration, if nothing set than 0 value would be used
@NOT_NULL
public Integer defaultNumber;

@KITTY_COLUMN(columnOrder = 4)
@DEFAULT(
        predefinedLiteralValue = LiteralValues.CURRENT_DATE
)
@NOT_NULL
public String creationDate;
{{< /highlight >}}
</details>

3. 
<details> 
  <summary>Click to view `UNIQUE` constraint declaration examples: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 1)
@NOT_NULL
@UNIQUE
public Long rndId;
{{< /highlight >}}
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cai")
@FOREIGN_KEY_T(
        name = "CAI_FK",
        columns = {IndexesAndConstraintsModel.RANDOM_ID_CNAME},
        reference = @FOREIGN_KEY_REFERENCE(
                foreignTableName = "random",
                foreignTableColumns = {"id"},
                onUpdate = OnUpdateDeleteActions.CASCADE,
                onDelete = OnUpdateDeleteActions.CASCADE
        )
)
@INDEX(indexColumns = {"creation_date"}) 
@UNIQUE_T(columns = {"rnd_id, animal"}) // Declaring unique constraint on more than two columns
public class IndexesAndConstraintsModel extends KittyModel {
    ...
}
{{< /highlight >}}
</details>

4. 
<details> 
  <summary>Click to view `CHECK` constraint declaration example: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 2)
@CHECK(checkExpression = "animal IN (\"CAT\", \"TIGER\", \"LION\")") // only cats allowed to this party
public Animals animal;
{{< /highlight >}}
</details>

5. 
<details> 
  <summary>Click to view `COLLATE` constraint declaration example: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 2)
@COLLATE(collation = BuiltInCollations.NOCASE) // Collation example
@CHECK(checkExpression = "animal IN (\"CAT\", \"TIGER\", \"LION\")") 
public Animals animal;
{{< /highlight >}}
</details>

6. 
<details> 
  <summary>Click to view `PRIMARY KEY` constraint declaration examples: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(
        columnOrder = 0, 
        isIPK = true
)
public Long id;
{{< /highlight >}}
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 0)
@PRIMARY_KEY
@NOT_NULL
public Long id;
{{< /highlight >}}
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cpk_test")
@PRIMARY_KEY_T(
    columns = {"user_name", "email"}
)
public class CPKModel extends KittyModel {

    @KITTY_COLUMN(columnOrder = 0)
    public String userName;

    @KITTY_COLUMN(columnOrder = 1)
    @UNIQUE
    public String email;
    
    ...
}
{{< /highlight >}}
</details>

7. 
<details> 
  <summary>Click to view `FOREIGN KEY` constraint declaration examples: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_COLUMN(columnOrder = 1)
@NOT_NULL
@UNIQUE
@FOREIGN_KEY(
        reference = @FOREIGN_KEY_REFERENCE(
                foreignTableName = "random",
                foreignTableColumns = {"id"},
                onUpdate = OnUpdateDeleteActions.CASCADE,
                onDelete = OnUpdateDeleteActions.CASCADE
        )
)
public Long rndId;
{{< /highlight >}}
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cai")
@FOREIGN_KEY_T(
        name = "CAI_FK",
        columns = {IndexesAndConstraintsModel.RANDOM_ID_CNAME},
        reference = @FOREIGN_KEY_REFERENCE(
                foreignTableName = "random",
                foreignTableColumns = {"id"},
                onUpdate = OnUpdateDeleteActions.CASCADE,
                onDelete = OnUpdateDeleteActions.CASCADE
        )
)
@INDEX(indexColumns = {"creation_date"})
public class IndexesAndConstraintsModel extends KittyModel {
    ...
    
    @KITTY_COLUMN(columnOrder = 1)
    @NOT_NULL
    @UNIQUE
    public Long rndId;

    ...
}
{{< /highlight >}}
</details>

8. 
<details> 
  <summary>Click to view index declaration examples: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cai")
@FOREIGN_KEY_T(
        name = "CAI_FK",
        columns = {IndexesAndConstraintsModel.RANDOM_ID_CNAME},
        reference = @FOREIGN_KEY_REFERENCE(
                foreignTableName = "random",
                foreignTableColumns = {"id"},
                onUpdate = OnUpdateDeleteActions.CASCADE,
                onDelete = OnUpdateDeleteActions.CASCADE
        )
)
@INDEX(indexColumns = {"creation_date"}) // index declaration
public class IndexesAndConstraintsModel extends KittyModel {
    ...

    @KITTY_COLUMN(columnOrder = 4)
    @DEFAULT(
            predefinedLiteralValue = LiteralValues.CURRENT_DATE
    )
    @NOT_NULL
    public String creationDate; // indexed column

    ...
}
{{< /highlight >}}
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cai")
...
public class IndexesAndConstraintsModel extends KittyModel {
    ...

    @KITTY_COLUMN(columnOrder = 5)
    @DEFAULT(
            predefinedLiteralValue = LiteralValues.CURRENT_TIMESTAMP
    )
    // One column indexe declaration example
    @ONE_COLUMN_INDEX(unique = true, indexName = "IAC_unique_index_creation_timestamp") 
    @NOT_NULL
    public Timestamp creationTmstmp;

    ...
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

5. 
<details> 
  <summary>Click to view `IndexesAndConstraintsModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE(tableName = "cai")
@FOREIGN_KEY_T(
        name = "CAI_FK",
        columns = {IndexesAndConstraintsModel.RANDOM_ID_CNAME},
        reference = @FOREIGN_KEY_REFERENCE(
                foreignTableName = "random",
                foreignTableColumns = {"id"},
                onUpdate = OnUpdateDeleteActions.CASCADE,
                onDelete = OnUpdateDeleteActions.CASCADE
        )
)
@INDEX(indexColumns = {"creation_date"})
public class IndexesAndConstraintsModel extends KittyModel {
    static final String RANDOM_ID_CNAME = "rnd_id";

    @KITTY_COLUMN(columnOrder = 0)
    @PRIMARY_KEY
    @NOT_NULL
    public Long id;

    @KITTY_COLUMN(columnOrder = 1)
    @NOT_NULL
    @UNIQUE
    public Long rndId;

    @KITTY_COLUMN(columnOrder = 2)
    @CHECK(checkExpression = "animal IN (\"CAT\", \"TIGER\", \"LION\")") // only cats allowed to this party
    public Animals animal;

    @KITTY_COLUMN(columnOrder = 3)
    @DEFAULT(signedInteger = 28) // You can choose for options for default declaration, if nothing set than 0 value would be used
    @NOT_NULL
    public Integer defaultNumber;

    @KITTY_COLUMN(columnOrder = 4)
    @DEFAULT(
            predefinedLiteralValue = LiteralValues.CURRENT_DATE
    )
    @NOT_NULL
    public String creationDate;

    @KITTY_COLUMN(columnOrder = 5)
    @DEFAULT(
            predefinedLiteralValue = LiteralValues.CURRENT_TIMESTAMP
    )
    @ONE_COLUMN_INDEX(unique = true, indexName = "IAC_unique_index_creation_timestamp")
    @NOT_NULL
    public Timestamp creationTmstmp;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("[ RowID = ").append(getRowID())
                .append(" ; id = ").append(id)
                .append(" ; rndId = ").append(rndId)
                .append(" ; animal = ").append(animal)
                .append(" ; defaultNumber = ").append(defaultNumber)
                .append(" ; creationDate = ").append(creationDate)
                .append(" ; creationTmstmp = ").append(creationTmstmp).append(" ]");
        return sb.toString();
    }
}
{{< /highlight >}} 
</details>

**Fragment code used in this tutorial**

1. 
<details> 
  <summary>Click to view `Lesson3Tab2Constraints.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class Lesson3Tab2Constraints extends Lesson3BaseFragment {

    private BasicDatabase database;

    protected ArrayAdapter<String> animalAdapter;
    protected Spinner animalSpinner;

    public Lesson3Tab2Constraints() {}

    EditText rndIdFkET;
    EditText defaultIntET;
    EditText creationDateET;
    EditText creationTmstmpET;

    Button saveNewModelButton;
    Button wipeAllButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson3_tab2_constraints_and_indexes, container, false);

        rndIdFkET = rootView.findViewById(R.id.l3_t2_et_fk);
        defaultIntET = rootView.findViewById(R.id.l3_t2_et_default_number);
        creationDateET = rootView.findViewById(R.id.l3_t2_et_creation_date);
        creationTmstmpET = rootView.findViewById(R.id.l3_t2_et_current_timestamp);

        saveNewModelButton = rootView.findViewById(R.id._l3_t2_save_button);
        saveNewModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndexesAndConstraintsModel model = getModelFromInput();
                if(model==null) return;
                new InsertNewAsync().execute(model);
            }
        });

        wipeAllButton = rootView.findViewById(R.id._l3_t2_wipe_button);
        wipeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WipeAsync().execute(0l);
            }
        });


        setUpExpandedList(
                rootView,
                R.id._l3_t2_expanded_panel_lw,
                R.id._l3_t2_expanded_panel_text,
                R.string._l3_t2_expanded_text_pattern
        );

        setAnimalSpinner(rootView, R.id.l3_t2_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        reloadTableExpandedList();
        return rootView;
    }

    private void reloadTableExpandedList() {
        new ReloadTableAsync().execute(0l);
    }

    private IndexesAndConstraintsModel getModelFromInput() {
        IndexesAndConstraintsModel model = new IndexesAndConstraintsModel();
        String rndId = rndIdFkET.getText().toString();
        if(rndId == null) model.rndId = null;
        else if(rndId.trim().length() == 0) model.rndId = null;
        else {
            try {
                model.rndId = Long.parseLong(rndId);
            } catch (Exception e) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_rnd_id_cant_be_treated_as_null_or_long_only,
                        R.string._warning_dialog_ok_button_text);
                return null;
            }
        }
        String animalEnumStringValue = animalSpinner.getSelectedItem().toString();
        if(!animalEnumStringValue.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            model.animal = Animals.valueOf(animalEnumStringValue);
        }
        String defInteger = defaultIntET.getText().toString();
        if(defInteger == null) model.setFieldExclusion("defaultNumber");
        else if(defInteger.trim().length() == 0) model.setFieldExclusion("defaultNumber");
        else {
            try {
                model.defaultNumber = Integer.parseInt(defInteger);
            } catch (Exception e) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_default_number_can_be_treated_as_null_or_long_only,
                        R.string._warning_dialog_ok_button_text);
                return null;
            }
        }
        String creationDate = creationDateET.getText().toString();
        if(creationDate == null) model.setFieldExclusion("creationDate");
        else if(creationDate.trim().length() == 0) model.setFieldExclusion("creationDate");
        else model.creationDate = creationDate;
        String creationTimestamp = creationTmstmpET.getText().toString();
        if(creationTimestamp == null) model.setFieldExclusion("creationTmstmp");
        else if(creationTimestamp.trim().length() == 0) model.setFieldExclusion("creationTmstmp");
        else {
            Long creationTimestampLong = null;
            try {
                creationTimestampLong = Long.parseLong(creationTimestamp);
            } catch (Exception e) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_creation_timestamp_can_be_treated_as_null_or_long_only,
                        R.string._warning_dialog_ok_button_text);
                return null;
            }
            model.creationTmstmp = new Timestamp(creationTimestampLong);
        }

        return model;
    }

    private BasicDatabase getDatabase() {
        if(database != null) return database;
        database = new BasicDatabase(getContext());
        return database;
    }

    @Override
    public void onVisible() {
        reloadTableExpandedList();
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l3_t2_snackbar_message;
    }

    // Asyncs

    class ReloadTableAsync extends AsyncTask<Long, Long, List<IndexesAndConstraintsModel>> {

        @Override
        protected List<IndexesAndConstraintsModel> doInBackground(Long... params) {
            KittyMapper mapper = Lesson3Tab2Constraints.this.getDatabase().getMapper(IndexesAndConstraintsModel.class);
            List<IndexesAndConstraintsModel> out = mapper.findAll();
            mapper.close();
            return out;
        }

        @Override
        protected void onPostExecute(List<IndexesAndConstraintsModel> result) {
            if(result != null) {
                events.setAdapter(new CAIModelAdapter(getContext(), result));
                expandedTitle.setText(format(expandeddTitlePattern, result.size()));
            } else {
                events.setAdapter(new CAIModelAdapter(getContext(), new LinkedList<IndexesAndConstraintsModel>()));
                expandedTitle.setText(format(expandeddTitlePattern, 0));
            }
        }
    }

    private static final String ERR_STRING_WIPE = "Lesson3tab2WipeDataError, see exception details!";

    class WipeAsync extends AsyncTask<Long, Long, Long> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(
                    Lesson3Tab2Constraints.this.getLessonActivity(),
                    Lesson3Tab2Constraints.this.getString(R.string._l3_t2_running_requested_operation_pg_title),
                    Lesson3Tab2Constraints.this.getString(R.string._l3_t2_running_requested_operation_pg_body)
            );
            dialog.setCancelable(false);
        }

        @Override
        protected Long doInBackground(Long... params) {
            try {
                KittyMapper mapper = Lesson3Tab2Constraints.this.getDatabase().getMapper(IndexesAndConstraintsModel.class);
                long affected = mapper.deleteAll();
                mapper.close();
                return affected;
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_STRING_WIPE, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return -1l;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            dialog.cancel();
            if (result <= -1l) {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_some_error_on_deleting,
                        R.string._warning_dialog_ok_button_text);
            } else {
                reloadTableExpandedList();
            }
        }
    }

    static final String IA_EXPECTED_ONLY_ONE = "Lesson3Tab2Constraints$InsertNewAsync expects array with one element as parameter for doInBackground";
    static final String ERR_ON_INSERTION = "Lesson3Tab2Constraints$InsertNewAsync error on insertion, see exception details!";

    class InsertNewAsync extends AsyncTask<IndexesAndConstraintsModel, Long, InsertNewAsyncResult> {
        @Override
        protected InsertNewAsyncResult doInBackground(IndexesAndConstraintsModel... params) {
            if(params.length > 1)
                throw new IllegalArgumentException(IA_EXPECTED_ONLY_ONE);
            try {
                KittyMapper mapper = getDatabase().getMapper(IndexesAndConstraintsModel.class);
                long insert = mapper.insert(params[0]);
                mapper.close();
                if(insert > -1l)
                    return new InsertNewAsyncResult(true, null, insert);
                else
                    return new InsertNewAsyncResult(false, null, insert);
            } catch (Exception e) {
                Log.e(BasicDatabase.LOG_TAG, ERR_ON_INSERTION, e);
                if(e instanceof KittyRuntimeException) {
                    if(((KittyRuntimeException) e).getNestedException() != null) {
                        Log.e(BasicDatabase.LOG_TAG, ERR_ON_INSERTION, ((KittyRuntimeException) e).getNestedException());
                    }
                }
                return new InsertNewAsyncResult(false, e, -1l);
            }
        }

        protected void onPostExecute(InsertNewAsyncResult result) {
            if(result.success)
                reloadTableExpandedList();
            else {
                getLessonActivity().showWarningDialog(
                        R.string._warning_dialog_title,
                        R.string._l3_t2_some_error_on_insertion,
                        R.string._warning_dialog_ok_button_text);
            }
        }
    }

    class InsertNewAsyncResult {
        boolean success;
        Exception exception;
        long insertId;

        public InsertNewAsyncResult(boolean success, Exception exception, long insertId) {
            this.success = success;
            this.exception = exception;
            this.insertId = insertId;
        }
    }

    // Animal spinner stuff
    protected ArrayAdapter<String> newAnimalAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1;
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        String[] adapterStrings = getContext().getResources().getStringArray(R.array.animal_enum);
        for(int i = 0; i < adapterStrings.length; i++) {
            adapter.add(adapterStrings[i]);
        }
        adapter.add(getContext().getString(R.string._l2_t1_random_animal_hint));
        return adapter;
    }

    protected void setAnimalSpinner(View rootView, int spinnerId, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        animalSpinner = (Spinner) rootView.findViewById(spinnerId);
        animalAdapter = newAnimalAdapter();
        animalSpinner.setAdapter(animalAdapter);
        animalSpinner.setSelection(animalAdapter.getCount()); //display hint
        animalSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    // expanded list
    CAIModelAdapter caiModelAdapter;

    @Override
    protected void setUpExpandedList(View rootView, int eventsId, int eventsTitleId, int eventTitleStringPattern) {
        events = (ListView) rootView.findViewById(eventsId);
        expandedTitle = (TextView) rootView.findViewById(eventsTitleId);
        expandeddTitlePattern = getString(eventTitleStringPattern);

        expandedTitle.setText(format(expandeddTitlePattern, 0));

        if(expandedAdapter == null) {
            caiModelAdapter = new CAIModelAdapter(getContext(), new LinkedList<IndexesAndConstraintsModel>());
        }

        events.setAdapter(caiModelAdapter);
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
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T2_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T2_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L3_T2_SCHEMA);
            }
        };
    }
}

{{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
