package br.edu.ifpe.garanhuns.aprendendo.bluetoothandroid;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

public class ConfiguracaoActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SEND_MESSAGE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    static TextView statusMessage;
    static TextView textSpace;

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    ConnectionThread connect;


    //Muda a mensagem status
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        statusMessage = (TextView) findViewById(R.id.statusMessage);
        textSpace = (TextView) findViewById(R.id.textSpace);
        if (btAdapter == null) {
            statusMessage.setText("O Bluetooth não está funcionando. É null.");
        } else {
            statusMessage.setText("Hardware Bluetooth está funcionando :)");
            if( !btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                statusMessage.setText("Solicitando ativação do Bluetooth...");
            } else {
                statusMessage.setText("Bluetooth já ativado :)");
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                statusMessage.setText("Bluetooth foi ativado");
            } else {
                statusMessage.setText("Bluetooth não ativado.");
            }
        } else if (requestCode == SELECT_DISCOVERED_DEVICE) {
            if (resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou" + data.getStringExtra("btDevName") + "\n" + data.getStringExtra("btDevAddress"));

                //Estabelece conexão como cliente
                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();
            } else {
                statusMessage.setText("Nenhum dispositivo Selecionado.");
            }
        }
    }


    public void discoverDevices(View view) {
        Intent searchDiscoveredDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchDiscoveredDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla o menu; adiciona itens a Action Bar se esta existir.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ? Handle action bar item clicks here. The action bar will
        // ? automatically handle clicks on the Home/Up button, so long
        // ? as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void waitConnection(View view) {
        //Espera conexão como servidor
        connect = new ConnectionThread();
        connect.start();
    }

    public void sendMessage(View view) {

        EditText messageBox = (EditText) findViewById(R.id.editText_MessageBox);
        String messageBoxString = messageBox.getText().toString();
        byte[] data = messageBoxString.getBytes();
        connect.write(data);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            if (dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão.");
            else if (dataString.equals("---S"))
                statusMessage.setText("Conectado.");
            else {
                textSpace.setText(new String(data));
            }
        }
    };

    //Alterar a visibilidade do dispositivo
    public void enableVisibility(View view){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        //Podemos variar de 1 a 3600s, mas 0 significa sempre visível
        startActivity(discoverableIntent);
    }
}
