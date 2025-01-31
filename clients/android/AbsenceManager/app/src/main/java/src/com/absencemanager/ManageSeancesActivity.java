package src.com.absencemanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ManageSeancesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_seances);

        Client client = Client.getInstance();
        ListView seancesListView = findViewById(R.id.seancesListView);
        EditText textStatus = new EditText(this); // Placeholder, assurez-vous de le lier correctement dans le layout

        // Créer et démarrer le thread pour récupérer les séances
        MyThreadGet myThreadGet = new MyThreadGet(client, "seances", this, seancesListView, textStatus);
        myThreadGet.start();
    }
}
