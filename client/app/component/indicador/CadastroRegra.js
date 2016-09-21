Ext.define('Nerif.component.indicador.CadastroRegra', {
    extend: 'Ext.window.Window',

    modal: true,
    layout: 'fit',
    title: 'Cadastro de regras',

    initComponent: function () {
        var obj = this;

        var propriedadeStore = Ext.create('Ext.data.Store', {
            fields: ['description', 'type', 'apache', 'nginx', 'iis'],
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '/src/config/properties.json',
                reader: {
                    type: 'json',
                    rootProperty: 'properties'
                }
            },
            sorters: [{
                property: 'description',
                direction: 'ASC'
            }],
            filters: [{
                filterFn: function (rec) {
                    var propriedade = rec.get(Gerenciador.get('server'));
                    return !!propriedade && Gerenciador.get('logFormat').indexOf(propriedade) !== -1;
                }
            }]
        });

        var propriedadeCombo = Ext.create('Ext.form.ComboBox', {
            name: 'infoPropriedade',
            allowBlank: false,
            editable: false,
            store: propriedadeStore,
            valueField: Gerenciador.get('server'),
            displayField: 'description',
            fieldLabel: 'Propriedade'
        });

        var cancelarBtn = Ext.create('Ext.button.Button', {
            text: 'Cancelar',
            handler: function () {
                obj.close();
            }
        });

        var confirmarBtn = Ext.create('Ext.button.Button', {
            text: 'Salvar',
            formBind: true,
            handler: function () {
                obj.fireEvent('regrasalva', formpanel.getValues());
                obj.close();
            }
        });

        var formpanel = Ext.create('Ext.form.Panel', {
            bodyPadding: '10px',
            fieldDefaults: {
                labelAlign: 'right',
                width: 400
            },
            items: [propriedadeCombo],
            buttons: ['->', cancelarBtn, confirmarBtn]
        });

        this.items = [formpanel];

        this.callParent();
    }
});