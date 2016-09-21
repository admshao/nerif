Ext.define('Nerif.component.indicador.Grid', {
    extend: 'Ext.grid.Panel',

    viewModel: {
        type: 'gerenciador'
    },

    //todo controle de id
    currentId: 0,

    bind: {
        store: '{indicators}'
    },

    columns: [{
        xtype: 'templatecolumn',
        text: 'Indicadores',
        flex: 1,
        tpl: '{descricao}'
    }],

    buttons: ['->', {
        text: 'Adicionar',
        bind: {
            disabled: '{!server}'
        },
        handler: function (me) {
            Ext.create('Nerif.component.indicador.CadastroIndicador', {
                listeners: {
                    'indicadorsalvo': function (dados) {
                        var grid = me.up('grid');

                        dados.id = grid.currentId++;
                        grid.getStore().add(dados);
                    }
                }
            }).show();
        }
    }]
});