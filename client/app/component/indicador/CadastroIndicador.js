Ext.define('Nerif.component.indicador.CadastroIndicador', {
    extend: 'Ext.window.Window',

    modal: true,
    layout: 'fit',
    title: 'Cadastro de indicadores',

    initComponent: function () {
        var obj = this;

        var indicadoridHdn = Ext.create('Ext.form.Hidden', {
            name: 'id'
        });

        var descricaoText = Ext.create('Ext.form.Text', {
            name: 'descricao',
            allowBlank: false,
            fieldLabel: 'Descrição'
        });

        var adicionarRegraBtn = Ext.create('Ext.button.Button', {
            text: 'Nova regra',
            handler: function () {
                Ext.create('Nerif.component.indicador.CadastroRegra', {
                    listeners: {
                        'regrasalva': function (dados) {
                            regrasStore.add(dados);
                        }
                    }
                }).show();
            }
        });

        var regrasStore = Ext.create('Ext.data.Store', {
            fields: ['infoPropriedade', 'tipoCategoria', 'tipoValor', 'valor1', 'valor2']
        });

        var regrasGrid = Ext.create('Ext.grid.Panel', {
            height: 250,
            store: regrasStore,
            columns: [{
                xtype: 'templatecolumn',
                text: 'Regras',
                flex: 1,
                tpl: '{infoPropriedade}'
            }],
            buttons: ['->', adicionarRegraBtn]
        });

        var cancelarBtn = Ext.create('Ext.button.Button', {
            text: 'Cancelar',
            handler: function () {
                obj.close();
            }
        });

        var confirmarBtn = Ext.create('Ext.button.Button', {
            formBind: true,
            text: 'Salvar',
            handler: function () {
                obj.fireEvent('indicadorsalvo', formpanel.getValues());
                obj.close();
            }
        });

        var formpanel = Ext.create('Ext.form.Panel', {
            bodyPadding: '10px',
            fieldDefaults: {
                labelAlign: 'right',
                width: 600
            },
            items: [indicadoridHdn, descricaoText, regrasGrid],
            buttons: ['->', cancelarBtn, confirmarBtn]
        });

        this.items = [formpanel];

        this.on('afterrender', function () {
            formpanel.isValid();
        });

        this.callParent();
    }
});