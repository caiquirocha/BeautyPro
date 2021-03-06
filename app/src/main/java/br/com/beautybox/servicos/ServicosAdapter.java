package br.com.beautybox.servicos;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.math.BigDecimal;

import br.com.beautybox.R;
import br.com.beautybox.dao.ServicoDAO;
import br.com.beautybox.domain.Servico;

/**
 * Created by lsimaocosta on 20/06/16.
 */
public class ServicosAdapter extends FirebaseListAdapter<Servico> {

    public ServicosAdapter(Activity activity, Query ref) {
        super(activity, Servico.class,R.layout.list_item_servicos,ref);
    }

    @Override
    protected Servico parseSnapshot(DataSnapshot snapshot) {
        return ServicoDAO.parseSnapshot(snapshot);
    }

    @Override
    protected void populateView(View view, Servico servico, int position) {
        final BigDecimal _100 = BigDecimal.valueOf(100);

        TextView txtServico = (TextView) view.findViewById(R.id.txt_servico);
        txtServico.setText(servico.getDescricao());

        TextView txtQtdeSessoes = (TextView) view.findViewById(R.id.txt_qtde_sessoes);
        if (servico.getQtdeSessoes() > 1) {
            txtQtdeSessoes.setVisibility(View.VISIBLE);
            txtQtdeSessoes.setText(servico.getQtdeSessoes() + " sessões");
        }else {
            txtQtdeSessoes.setVisibility(View.GONE);
        }

        final BigDecimal valorAVistaEmCentavos = BigDecimal.valueOf(servico.getValorAVista());
        TextView txtValorAVista = (TextView) view.findViewById(R.id.txt_valor_a_vista);
        txtValorAVista.setText("R$ " + valorAVistaEmCentavos.divide(_100).toString());

        final BigDecimal valorAPrazoEmCentavos = BigDecimal.valueOf(servico.getValorAPrazo());
        TextView txtValorAPrazo = (TextView) view.findViewById(R.id.txt_valor_a_prazo);
        txtValorAPrazo.setText("R$ " + valorAPrazoEmCentavos.divide(_100).toString());
    }
}
