---
title: "Lesson2 tab 2 sources"
date: 2018-11-15T06:50:21+03:00
draft: false
layout: "lesson"
---
**KittyORM update example**

1. 
{{< highlight java "linenos=inline, linenostart=1">}}
// Initializing database instance
BasicDatabase db = new BasicDatabase(getContext());
// Getting mapper instance
RandomMapper mapper = (RandomMapper) db.getMapper(RandomModel.class);
// Getting existing model from database (assuming that 0l model exists)
RandomModel toUpdate = mapper.findByIPK(0l);
// Setting model fields
toUpdate.randomInt = 12;
...
// Saving model with save method
mapper.save(toUpdate);
// Saving model with direct insert call
mapper.update(toInsert);
{{< /highlight >}}

2. 
{{< highlight java "linenos=inline, linenostart=1">}}
// Initializing database instance
BasicDatabase db = new BasicDatabase(getContext());
// Getting mapper instance
RandomMapper mapper = (RandomMapper) db.getMapper(RandomModel.class);
// Creating condition builder instance
SQLiteConditionBuilder builder = new SQLiteConditionBuilder();
builder.addColumn("id")
       .addSQLOperator(SQLiteOperator.GREATER_OR_EQUAL)
       .addValue(10)
       .addSQLOperator(SQLiteOperator.AND)
       .addColumn("id")
       .addSQLOperator(SQLiteOperator.LESS_OR_EQUAL)
       .addValue(20);
// Creating blank model and setting it fields
RandomModel toUpdate = new RandomModel();
toUpdate.randomInt = 50;
// Updating table with custom clause and values from model
mapper.update(toUpdate, builder.build(), new String[]{"randomInt"}, CVUtils.INCLUDE_ONLY_SELECTED_FIELDS);
{{< /highlight >}}


**KittyORM `basic_database` implementation sources**

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
  <summary>Click to view `RandomModel.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
@KITTY_TABLE
@KITTY_EXTENDED_CRUD(extendedCrudController = RandomMapper.class)
@INDEX(
        indexName = "random_animal_index",
        indexColumns = {AbstractRandomModel.RND_ANIMAL_CNAME}
)
public class RandomModel extends AbstractRandomModel {


    public RandomModel() {
        super();
    }

    @KITTY_COLUMN(columnOrder = 5)
    public String randomAnimalSays;

    @Override
    public String toString() {
        return new StringBuffer(64).append("[ id = ")
                                            .append(id)
                                            .append("; randomInt = ")
                                            .append(Integer.toString(randomInt))
                                            .append("; randomInteger = ")
                                            .append(randomInteger)
                                            .append("; randomAnimal = ")
                                            .append(randomAnimal)
                                            .append("; randomAnimnalLocalizedName = ")
                                            .append(randomAnimalName)
                                            .append("; randomAnimalSays = ")
                                            .append(randomAnimalSays).append(" ]").toString();
    }
}
{{< /highlight >}} 
</details>

4. 
<details> 
  <summary>Click to view `RandomMapper.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
package net.akaish.kittyormdemo.sqlite.basicdb;

import net.akaish.kitty.orm.KittyMapper;
import net.akaish.kitty.orm.KittyModel;
import net.akaish.kitty.orm.configuration.conf.KittyTableConfiguration;
import net.akaish.kitty.orm.query.QueryParameters;
import net.akaish.kitty.orm.query.conditions.SQLiteCondition;
import net.akaish.kitty.orm.query.conditions.SQLiteConditionBuilder;
import net.akaish.kitty.orm.enums.SQLiteOperator;
import net.akaish.kitty.orm.util.KittyConstants;
import net.akaish.kittyormdemo.sqlite.misc.Animals;

import static net.akaish.kitty.orm.enums.SQLiteOperator.AND;
import static net.akaish.kitty.orm.enums.SQLiteOperator.LESS_OR_EQUAL;
import static net.akaish.kitty.orm.enums.SQLiteOperator.LESS_THAN;
import static net.akaish.kitty.orm.enums.SQLiteOperator.GREATER_OR_EQUAL;
import static net.akaish.kitty.orm.enums.SQLiteOperator.GREATER_THAN;
import static net.akaish.kittyormdemo.sqlite.basicdb.AbstractRandomModel.RND_ANIMAL_CNAME;

import java.util.List;


/**
 * Created by akaish on 09.08.18.
 * @author akaish (Denis Bogomolov)
 */
public class RandomMapper extends KittyMapper {

    public <M extends KittyModel> RandomMapper(KittyTableConfiguration tableConfiguration,
                                              M blankModelInstance,
                                              String databasePassword) {
        super(tableConfiguration, blankModelInstance, databasePassword);
    }

    protected SQLiteCondition getAnimalCondition(Animals animal) {
        return new SQLiteConditionBuilder()
                .addColumn(RND_ANIMAL_CNAME)
                .addSQLOperator("=")
                .addObjectValue(animal)
                .build();
    }

    public long deleteByRandomIntegerRange(int start, int end) {
        return deleteWhere("#?randomInt >= ? AND #?randomInt <= ?", start, end);
    }

    public long deleteByAnimal(Animals animal) {
        return deleteWhere(getAnimalCondition(animal));
    }

    public List<RandomModel> findByAnimal(Animals animal, long offset, long limit, boolean groupingOn) {
        SQLiteCondition condition = getAnimalCondition(animal);
        QueryParameters qparam = new QueryParameters();
        qparam.setLimit(limit).setOffset(offset);
        if(groupingOn)
            qparam.setGroupByColumns(RND_ANIMAL_CNAME);
        else
            qparam.setGroupByColumns(KittyConstants.ROWID);
        return findWhere(condition, qparam);
    }

    public List<RandomModel> findByIdRange(long fromId, long toId, boolean inclusive, Long offset, Long limit) {
        SQLiteCondition condition = new SQLiteConditionBuilder()
                .addColumn("id")
                .addSQLOperator(inclusive ? GREATER_OR_EQUAL : GREATER_THAN)
                .addValue(fromId)
                .addSQLOperator(AND)
                .addColumn("id")
                .addSQLOperator(inclusive ? LESS_OR_EQUAL : LESS_THAN)
                .addValue(toId)
                .build();
        QueryParameters qparam = new QueryParameters();
        qparam.setLimit(limit).setOffset(offset).setGroupByColumns(KittyConstants.ROWID);
        return findWhere(condition, qparam);
    }

    public List<RandomModel> findAllRandomModels(Long offset, Long limit) {
        QueryParameters qparam = new QueryParameters();
        qparam.setLimit(limit).setOffset(offset).setGroupByColumns(KittyConstants.ROWID);
        return findAll(qparam);
    }

}
{{< /highlight >}} 
</details>

5. 
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

6. 
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


**Fragment and utility code used in this tutorial**

1. 
<details> 
  <summary>Click to view `RNDRandomModelFactory.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class RNDRandomModelFactory {

    private final Context context;
    private final Random randomizer;

    private final SparseArray<String> randomAnimalSays = new SparseArray<>();
    private final SparseArray<String> randomAnimalLocalizedName = new SparseArray<>();

    public RNDRandomModelFactory(Context context) {
        super();
        this.context = context;
        this.randomizer = new Random();

        // Lol, getContext().getString() method is slow, calling for each new random model this method twice causes 55% of all execution time of generating new random model (!)
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

    public RandomModel newRandomModel() {
        RandomModel out = new RandomModel();
        out.randomInt = randomizer.nextInt();
        out.randomInteger = randomizer.nextInt();
        out.randomAnimal = Animals.rndAnimal(randomizer);
        out.randomAnimalSays = randomAnimalSays.get(Animals.getLocalizedAnimalSaysResource(out.randomAnimal));
        out.randomAnimalName = randomAnimalLocalizedName.get(Animals.getLocalizedAnimalNameResource(out.randomAnimal));
        return out;
    }
}
{{< /highlight >}} 
</details>

2. 
<details> 
  <summary>Click to view `Lesson2Tab2Update.class`: </summary>
{{< highlight java "linenos=inline, linenostart=1">}}
public class Lesson2Tab2Update extends Lesson2BaseFragment {

    public Lesson2Tab2Update(){}

    Button loadModelButton;
    Button updateButton;

    EditText loadModelIdET;

    EditText randomIntET;
    EditText randomIntegerET;
    EditText randomAnimalNameET;
    EditText randomAnimalSaysET;

    TextView loadedModelTW;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lesson2_tab2_update, container, false);
        setAnimalSpinner(rootView, R.id.l2_t2_spinner, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String enumStringName = (String) animalSpinner.getAdapter().getItem(position);
                if(enumStringName.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
                    // do nothing, skip spinner hint
                } else {
                    Animals animal = Animals.valueOf(enumStringName);
                    randomAnimalNameET.setText(Animals.getLocalizedAnimalNameResource(animal));
                    randomAnimalSaysET.setText(Animals.getLocalizedAnimalSaysResource(animal));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadedModelTW = rootView.findViewById(R.id.l2_t2_tw_current_model);
        loadModelButton = rootView.findViewById(R.id.l2_t2_load_button);

        loadModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadModel();
            }
        });

        updateButton = rootView.findViewById(R.id.l2_t2_update_model_button);

        updateButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                updateModel();
            }
        });

        randomIntET = rootView.findViewById(R.id.l2_t2_et_random_int);
        randomIntegerET = rootView.findViewById(R.id.l2_t2_et_random_integer);

        randomAnimalNameET = rootView.findViewById(R.id.l2_t2_et_animal_localised_name);
        randomAnimalSaysET = rootView.findViewById(R.id.l2_t2_et_animal_says);

        loadModelIdET = rootView.findViewById(R.id.l2_t2_et_id);

        setUpExpandedList(
                rootView,
                R.id._l2_t2_expanded_panel_lw,
                R.id._l2_t2_expanded_panel_text,
                R.string._l2_t2_expanded_text_pattern
        );

        return rootView;
    }

    void updateModel() {
        String randomInt = randomIntET.getText().toString();
        String randomInteger = randomIntegerET.getText().toString();
        String animalEnumStringValue = animalSpinner.getSelectedItem().toString();

        if(animalEnumStringValue.equals(animalAdapter.getItem(animalAdapter.getCount()))) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(randomInt == null || randomInteger == null) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(randomInt.length() == 0 || randomInteger.length() == 0) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_text, R.string._warning_dialog_ok_button_text);
            return;
        }
        int rndInt = 0; Integer rndInteger = null;
        try {
            rndInt = Integer.parseInt(randomInt);
            rndInteger = Integer.valueOf(randomInteger);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(R.string._warning_dialog_title, R.string._l2_t1_warning_bad_input, R.string._warning_dialog_ok_button_text);
            return;
        }
        if(isLoadedModelExists()) {
            RandomMapper mapper = getMapper();
            RandomModel toUpdate = mapper.findByIPK(getLoadedModelId());
            RandomModel originalModel = toUpdate.clone(RandomModel.class);
            toUpdate.randomInt = rndInt;
            toUpdate.randomInteger = rndInteger;
            Animals animal = Animals.valueOf(animalEnumStringValue);
            toUpdate.randomAnimal = animal;
            toUpdate.randomAnimalName = getString(Animals.getLocalizedAnimalNameResource(animal));
            toUpdate.randomAnimalSays = getString(Animals.getLocalizedAnimalSaysResource(animal));
            long updateStatus = mapper.update(toUpdate);
            if(updateStatus > 0) {
                addNewEventToExpandedPanel(format(getString(R.string._l2_t2_expanded_added), toUpdate.getRowID(), originalModel, toUpdate, mapper.countAll()));
            } else {
                addNewEventToExpandedPanel(format(getString(R.string._l2_t2_expanded_error), toUpdate));
            }
            mapper.close();
            if(isLoadedModelExists()) {
                modelExists();
            } else {
                noModel();
            }
        } else {
            addNewEventToExpandedPanel(format(getString(R.string._l2_t2_expanded_error_unable), getLoadedModelId()));
        }
    }

    void loadModel() {
        String modelIdFromLoadET = loadModelIdET.getText().toString();
        if(modelIdFromLoadET == null) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_bad_id_input,
                    R.string._warning_dialog_ok_button_text);
            return;
        }
        if(modelIdFromLoadET.length() == 0) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_bad_id_input,
                    R.string._warning_dialog_ok_button_text);
            return;
        }
        Long modelToLoad = null;
        try {
            modelToLoad = Long.valueOf(modelIdFromLoadET);
        } catch (Exception e) {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_bad_id_input,
                    R.string._warning_dialog_ok_button_text);
            return;
        }
        setLoadedModelId(modelToLoad);
        if(isLoadedModelExists())
            modelExists();
        else {
            getLessonActivity().showWarningDialog(
                    R.string._warning_dialog_title,
                    R.string._l2_t2_warning_no_record_with_id,
                    R.string._warning_dialog_ok_button_text);
            noModel();
        }
    }

    @Override
    public void onVisible() {
        new LoadSelectedModel().execute(0l);
    }

    void modelExists() {
        setUpdateFieldsActive(true);
        loadModelData(getLoadedModelId());
    }

    void noModel() {
        setUpdateFieldsActive(false);
        setLoadedModelTWText(null);
    }

    void loadModelData(Long IPK) {
        RandomMapper mapper = getMapper();
        RandomModel model = mapper.findByIPK(IPK);
        if(model == null) {
            setUpdateFieldsActive(false);
        } else {
            loadModelValues(model);
        }
        setLoadedModelTWText(model);
    }

    void setLoadedModelTWText(RandomModel model) {
        if(model == null) {
            loadedModelTW.setText(
                    format(
                            getString(R.string._l2_t2_current_model_pattern),
                            getString(R.string._l2_t2_current_model_not_set)
                    )
            );
        } else {
            loadedModelTW.setText(
                    format(
                            getString(R.string._l2_t2_current_model_pattern),
                            model
                    )
            );
        }
    }

    void loadModelValues(RandomModel model) {
        loadModelIdET.setText(Long.toString(model.id));
        randomIntET.setText(Integer.toString(model.randomInt));
        randomIntegerET.setText(Integer.toString(model.randomInteger));
        randomAnimalSaysET.setText(model.randomAnimalSays);
        randomAnimalNameET.setText(model.randomAnimalName);
        animalSpinner.setSelection(animalAdapter.getPosition(model.randomAnimal.name()), true);
    }

    void setUpdateFieldsActive(boolean isActive) {
        //randomIntET.setFocusable(isActive);
        randomIntET.setEnabled(isActive);

        //randomIntegerET.setFocusable(isActive);
        randomIntegerET.setEnabled(isActive);

        //updateButton.setFocusable(isActive);
        updateButton.setEnabled(isActive);

        //animalSpinner.setFocusable(isActive);
        animalSpinner.setEnabled(isActive);
        if(!isActive)
            animalSpinner.setVisibility(View.GONE);
        else
            animalSpinner.setVisibility(View.VISIBLE);

        randomAnimalSaysET.setEnabled(isActive);
        randomAnimalNameET.setEnabled(isActive);
    }

    // Fab menu section

    // Fab menu section

    @Override
    public View.OnClickListener helpFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T2_TUTORIAL);
            }
        };
    }

    @Override
    public View.OnClickListener sourceFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T2_SOURCE);
            }
        };
    }

    @Override
    public View.OnClickListener schemaFabMenuAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KittyTutorialActivity)getActivity()).showWebViewDialog(LessonsUriConstants.L2_T2_SCHEMA);
            }
        };
    }

    @Override
    protected int snackbarMessageResource() {
        return R.string._l2_t2_snackbar_message;
    }

    // Assyncs
    private static String LOG_E_EXCEPTION_ON_LOAD = "Error on loading initial model at Lesson2Tab2Update, see exception details: ";
    private static String LOG_E_EXCEPTION_ON_LOAD_NESTED = "Error on loading initial model at Lesson2Tab2Update, see exception details (nested exception): ";

    class LoadSelectedModel extends AsyncTask<Long, Long, RandomModel> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {}

        @Override
        protected RandomModel doInBackground(Long... params) {
            Long modelToLoad = getLoadedModelId();
            if(modelToLoad == null) return null;
            else {
                try {
                    RandomMapper mapper = (RandomMapper) getDb().getMapper(RandomModel.class);
                    return mapper.findByIPK(modelToLoad);
                } catch (Exception e) {
                    Log.e(BasicDatabase.LOG_TAG, LOG_E_EXCEPTION_ON_LOAD, e);
                    if(e instanceof KittyRuntimeException) {
                        if(((KittyRuntimeException) e).getNestedException() != null)
                            Log.e(BasicDatabase.LOG_TAG,
                                    LOG_E_EXCEPTION_ON_LOAD_NESTED,
                                    ((KittyRuntimeException) e).getNestedException());
                    }
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(RandomModel result) {
            if (result != null) {
                loadModelValues(result);
                setLoadedModelTWText(result);
            } else {
                setUpdateFieldsActive(false);
                setLoadedModelTWText(null);
            }
        }
    }
}
{{< /highlight >}} 
</details>

This page is a part of KittyORM project (KittyORM Documentation) and licensed under a Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, US., more information at https://akaish.github.io/KittyORMPages/license/
