package br.com.beautybox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.beautybox.atendimentos.AtendimentosListFragment;
import br.com.beautybox.clientes.ClientesListFragment;
import br.com.beautybox.main.MainFragment;
import br.com.beautybox.servicos.ServicosListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MAIN_FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private ActionBarDrawerToggle hamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment(), MAIN_FRAGMENT_TAG).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener());

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        hamburger = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(hamburger);
        hamburger.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void hideDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        hamburger.setDrawerIndicatorEnabled(false);
        hamburger.syncState();
    }

    public void showDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        hamburger.setDrawerIndicatorEnabled(true);
        hamburger.syncState();
    }

    @NonNull
    private FragmentManager.OnBackStackChangedListener onBackStackChangedListener() {
        return new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

                FragmentManager supportFragmentManager = getSupportFragmentManager();
                Fragment fr = supportFragmentManager.findFragmentById(R.id.fragment_container);

                if (fr instanceof MainFragment) {
                    getSupportActionBar().setTitle("Beauty Box");

                    // apagando a seleção do menu lateral
                    navigationView.getMenu().findItem(R.id.nav_servicos).setChecked(false);
                    navigationView.getMenu().findItem(R.id.nav_clientes).setChecked(false);
                    navigationView.getMenu().findItem(R.id.nav_atendimentos).setChecked(false);
                }
                else if (fr instanceof ServicosListFragment) {
                    getSupportActionBar().setTitle("Serviços");
                    navigationView.setCheckedItem(R.id.nav_servicos);
                }
                else if (fr instanceof ClientesListFragment) {
                    getSupportActionBar().setTitle("Clientes");
                    navigationView.setCheckedItem(R.id.nav_clientes);
                }
                else if (fr instanceof AtendimentosListFragment) {
                    getSupportActionBar().setTitle("Atendimentos");
                    navigationView.setCheckedItem(R.id.nav_atendimentos);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;

        switch (id) {
            case R.id.nav_atendimentos:
                fragment = AtendimentosListFragment.newInstance();
                break;
            case R.id.nav_servicos:
                fragment = ServicosListFragment.newInstance();
                break;
            case R.id.nav_clientes:
                fragment = new ClientesListFragment();
                break;
        }

        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
