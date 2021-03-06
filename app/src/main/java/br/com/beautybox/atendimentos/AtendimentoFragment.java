package br.com.beautybox.atendimentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.beautybox.R;
import br.com.beautybox.domain.Atendimento;
import br.com.beautybox.domain.Cliente;
import br.com.beautybox.domain.FormaPagamento;
import br.com.beautybox.domain.ItemServico;
import br.com.beautybox.domain.Servico;
import br.com.beautybox.domain.Sessao;

/**
 * Created by lsimaocosta on 22/06/16.
 */

public class AtendimentoFragment extends Fragment implements AtendimentoTabListener {

    private static final String TAG = AtendimentoFragment.class.getSimpleName();
    private Atendimento atendimento;
    private SessoesAdapter adapter;

    private SessaoDialogFragment.OnAdicionarSessaoListener onAdicionarSessaoListener = new SessaoDialogFragment.OnAdicionarSessaoListener() {
        @Override
        public void add(Sessao sessao, boolean isEditing) {

            // so manda adicionar caso nao esteja no modo edição,
            // pq se estiver editando o objeto ja esta na lista de sessões do atendimento
            if (!isEditing) {
                atendimento.addSessao(sessao);

                if (sessao.getServicos().size() == 1) {

                    ItemServico itemServico = sessao.getServicos().get(0);

                    Servico servico = itemServico.getServico();
                    int qtdeSessoes = itemServico.getServico().getQtdeSessoes();
                    int qtde = itemServico.getQuantidade();

                    boolean isPacote = (qtdeSessoes > 1) && (qtde == 1);

                    if (isPacote) {
                        Calendar c = new GregorianCalendar();
                        c.setTimeInMillis(sessao.getTimestamp());

                        for (int i = 0; i < qtdeSessoes - 1; ++i) {
                            c.add(Calendar.DAY_OF_MONTH, 1);

                            Sessao novaSessao = new Sessao();
                            novaSessao.setAtendimento(atendimento);
                            novaSessao.setTimestamp(c.getTime().getTime());

                            List<ItemServico> itens = new ArrayList<>();
                            ItemServico item = new ItemServico(servico, 1, 0, 0);
                            itens.add(item);

                            novaSessao.setServicos(itens);

                            atendimento.addSessao(novaSessao);
                        }
                    }
                }
            }

            Collections.sort(atendimento.getSessoes());
            adapter.notifyDataSetChanged();
        }
    };

    private SessoesAdapter.OnEditSessaoListener onEditSessaoListener = new SessoesAdapter.OnEditSessaoListener() {
        @Override
        public void edit(Sessao sessao) {
            showSessaoDialog(sessao);
        }
    };

    public static AtendimentoFragment newInstance(Atendimento atendimento) {
        AtendimentoFragment fragment = new AtendimentoFragment();
        fragment.atendimento = atendimento;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_atendimento, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(onClickFAB());

        TextView textViewNomeCliente = (TextView) getView().findViewById(R.id.edit_cliente);
        textViewNomeCliente.setText(atendimento.getCliente().getNome());

        Spinner spinnerFormaPagamento = (Spinner) view.findViewById(R.id.spinner_forma_pagamento);
        spinnerFormaPagamento.setAdapter(new ArrayAdapter<FormaPagamento>(getActivity(),
                android.R.layout.simple_list_item_1, FormaPagamento.values()));

        FormaPagamento formaPagamento = atendimento.getFormaPagamento();
        int index = 0;
        switch (formaPagamento) {
            case AVista:
                index = 0;
                break;
            case APrazo:
                index = 1;
                break;
            case Debito:
                index = 2;
                break;
            case APrazo1X:
                index = 3;
                break;
        }
        spinnerFormaPagamento.setSelection(index);
        spinnerFormaPagamento.setOnItemSelectedListener(onSelectFormaPagamento());


        adapter = new SessoesAdapter(getActivity(), atendimento, onEditSessaoListener);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                atualizarValorTotal();
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        atualizarValorTotal();
    }

    private AdapterView.OnItemSelectedListener onSelectFormaPagamento() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                FormaPagamento[] values = FormaPagamento.values();
                FormaPagamento fp = values[position];

                atendimento.setFormaPagamento(fp);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    /**
     * Abrir o editor de sessões
     *
     * @return
     */
    private View.OnClickListener onClickFAB() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSessaoDialog(null);
            }
        };
    }

    private void showSessaoDialog(Sessao sessao) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        SessaoDialogFragment.newInstance(sessao, onAdicionarSessaoListener).show(fragmentManager, "dialog");
    }

    void atualizarValorTotal() {

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        TextView txtValorTotal = (TextView) getView().findViewById(R.id.txt_valor_total);
        Spinner spinnerFormaPagamento = (Spinner) getView().findViewById(R.id.spinner_forma_pagamento);

        FormaPagamento formaPagamento = (FormaPagamento) spinnerFormaPagamento.getSelectedItem();

        atendimento.setFormaPagamento(formaPagamento);

        txtValorTotal.setText(numberFormat.format(atendimento.getValorTotal() / 100.0));
    }

    @Override
    public void writeChanges() {
        TextView textViewNomeCliente = (TextView) getView().findViewById(R.id.edit_cliente);
        String nomeCliente = textViewNomeCliente.getText().toString();

        Cliente cliente = atendimento.getCliente();
        cliente.setNome(nomeCliente);

        Spinner spinnerFormaPagamento = (Spinner) getView().findViewById(R.id.spinner_forma_pagamento);
        FormaPagamento formaPagamento = (FormaPagamento) spinnerFormaPagamento.getSelectedItem();
        atendimento.setFormaPagamento(formaPagamento);

    }

    @Override
    public boolean validate() {
        TextView textViewNomeCliente = (TextView) getView().findViewById(R.id.edit_cliente);

        if (TextUtils.isEmpty(textViewNomeCliente.getText())) {
            Toast.makeText(getContext(), "Informe o nome da cliente", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (atendimento.getSessoes().size() == 0) {
            Toast.makeText(getContext(), "Ao menos 1 sessão deve ser criada", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
