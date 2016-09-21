Ext.define('Nerif.component.servidor.Dados', {
    extend: 'Ext.form.FieldContainer',

    viewModel: {
        type: 'gerenciador'
    },

    bodyPadding: '10px',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initComponent: function () {
        var obj = this;

        var servidorStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'name'],
            data: [
                { id: 'apache', name: 'Apache' },
                { id: 'nginx', name: 'Nginx' },
                { id: 'iis', name: 'Windows Server' }
            ]
        });

        var servidorCombo = Ext.create('Ext.form.ComboBox', {
            allowBlank: false,
            store: servidorStore,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            fieldLabel: 'Servidor',
            bind: {
                value: '{server}'
            },
            listeners: {
                'select': function (me, record) {
                    diretorioLogText.enable();
                    diretorioLogText.setValue(null);
                    diretorioLogText.validate();

                    formatoLogTag.enable();
                    formatoLogTag.suspendEvent('beforedeselect');
                    formatoLogTag.setValue(null);
                    formatoLogTag.resumeEvent('beforedeselect');
                    formatoLogTag.validate();

                    formatoLogTag.valueField = record.data.id;

                    formatoLogStore.clearFilter();
                    formatoLogStore.filterBy(function (rec) {
                        return !!rec.get(formatoLogTag.valueField);
                    });
                }
            }
        });

        var diretorioLogText = Ext.create('Ext.form.Text', {
            allowBlank: false,
            fieldLabel: 'Diretório',
            disabled: true,
            bind: {
                value: '{logDirectory}'
            }
        });

        var formatoLogStore = Ext.create('Ext.data.Store', {
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
            }]
        });

        var formatoLogTag = Ext.create('Ext.form.field.Tag', {
            store: formatoLogStore,
            allowBlank: false,
            filterPickList: true,
            fieldLabel: 'Formato',
            valueField: '',
            displayField: 'description',
            disabled: true,
            bind: {
                value: '{logFormat}'
            },
            listeners: {
                'beforedeselect': function (me, record) {
                    Ext.Msg.confirm('ATENÇÃO', 'Esta ação irá remover todas as regras vinculadas a esta propriedades nos indicadores cadastrados. Deseja continuar?', function (btn) {
                        if (btn === 'yes') {
                            obj.fireEvent('formatoremovido', record.data.id);

                            me.suspendEvent('beforedeselect');
                            me.removeValue(record);
                            me.resumeEvent('beforedeselect');
                        }
                    });

                    return false;
                }
            }
        });

        var infoPanel = Ext.create('Ext.form.FieldContainer', {
            flex: 1,
            fieldDefaults: {
                labelAlign: 'right',
                width: '100%'
            },
            items: [servidorCombo, diretorioLogText, formatoLogTag]
        });

        var ajudaPanel = Ext.create('Ext.panel.Panel', {
            flex: 1,
            margin: '10px',
            bind: {
                html: '{serverDescription}'
            }
        });

        this.items = [infoPanel, ajudaPanel];

        this.callParent();
    }
});