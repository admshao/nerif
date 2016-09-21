Ext.define('Nerif.component.grupo.Cadastro', {
    extend: 'Ext.window.Window',

    modal: true,
    layout: 'fit',
    title: 'Cadastro de grupos',

    width: 600,
    height: 400,

    closeAction: 'hide',

    viewModel: {
        type: 'gerenciador'
    },

    initComponent: function () {
        var obj = this;

        var grupoidHdn = Ext.create('Ext.form.Hidden', {
            name: 'id'
        });

        var usuariosidsHdn = Ext.create('Ext.form.Hidden', {
            name: 'users'
        });

        var indicadoresidsHdn = Ext.create('Ext.form.Hidden', {
            name: 'indicators'
        });

        var descricaoText = Ext.create('Ext.form.Text', {
            region: 'north',
            allowBlank: false,
            name: 'descricao',
            fieldLabel: 'Descricao'
        });

        var usuariosGrid = Ext.create('Ext.grid.Panel', {
            flex: 1,
            bind: {
                store: '{users}'
            },
            columns: [{
                flex: 1,
                text: 'Usuários',
                dataIndex: 'nome'
            }],
            selModel: {
                selType: 'checkboxmodel',
                checkOnly: true
            },
            listeners: {
                'selectionchange': function (me, records) {
                    var userids = Ext.Array.pluck(Ext.Array.pluck(records, 'data'), 'id');
                    usuariosidsHdn.setValue(userids);
                }
            }
        });

        var indicadoresGrid = Ext.create('Ext.grid.Panel', {
            flex: 1,
            bind: {
                store: '{indicators}'
            },
            columns: [{
                flex: 1,
                text: 'Indicadores',
                dataIndex: 'descricao'
            }],
            selModel: {
                selType: 'checkboxmodel',
                checkOnly: true
            },
            listeners: {
                'selectionchange': function (me, records) {
                    var indicatorids = Ext.Array.pluck(Ext.Array.pluck(records, 'data'), 'id');
                    indicadoresidsHdn.setValue(indicatorids);
                }
            }
        });

        var usuariosIndicadoresPanel = Ext.create('Ext.form.FieldContainer', {
            region: 'center',
            padding: '5px 0',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            defaults: {
                margin: '5px'
            },
            items: [usuariosGrid, indicadoresGrid]
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
                obj.fireEvent('gruposalvo', formpanel.getValues());
                obj.close();
            }
        });

        var formpanel = Ext.create('Ext.form.Panel', {
            layout: 'border',
            bodyPadding: '10px',
            fieldDefaults: {
                labelAlign: 'right'
            },
            items: [grupoidHdn, usuariosidsHdn, indicadoresidsHdn, descricaoText, usuariosIndicadoresPanel],
            buttons: ['->', cancelarBtn, confirmarBtn]
        });

        this.items = [formpanel];

        this.on('afterrender', function () {
            formpanel.isValid();
        });

        this.callParent();
    }
});