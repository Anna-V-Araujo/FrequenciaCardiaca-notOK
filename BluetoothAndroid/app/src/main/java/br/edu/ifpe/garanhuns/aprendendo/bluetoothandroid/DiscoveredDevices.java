package br.edu.ifpe.garanhuns.aprendendo.bluetoothandroid;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Anna Vitória on 11/08/2016.
 */
public class DiscoveredDevices extends ListActivity {
    // Dispositivos encontrados na busca



    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        //Adiciciona um título a lista
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.activity_configuracao, lv, false);
        ((EditText) header.findViewById(R.id.editText_MessageBox)).setText("\nDispositivos próximos\n");
        lv.addHeaderView(header, null, false);

        //Modelo da lista
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        setListAdapter(arrayAdapter);


        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery(); // Dura apenas 12s

        //Filtro captura o momento em que um dispositivo é descoberto
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        
    }

    //Extrair nome e endereço partindo do conteudo do elemento selecionado
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){

        String item = (String) getListAdapter().getItem(position -1);
        String devName = item.substring(0, item.indexOf("\n"));
        String devAddress = item.substring(item.indexOf("\n")+1, item.length());

        //Usa Intent para encapsular as informações de nome e endereço
        //Informar resultado a Activity Principal, finalizando e voltando.
        Intent returnIntent = new Intent();
        returnIntent.putExtra("btDevName", devName);
        returnIntent.putExtra("btDevAddress", devAddress);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    //Receptor do evento de descoberta de dispositivo
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Obter o Intent que gerou a ação / Verificar a descoberta de um novo dispositivo
            //Obter Objeto Descoberto / Exibir Nome e Endereço

            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

        }
    };

    //Quando a activity for encerrada

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //Remover (encerrar) filtro de descoberta

        unregisterReceiver(receiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //Inflar menu, adicionando itens na barra de ações se estiver presente
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



}
