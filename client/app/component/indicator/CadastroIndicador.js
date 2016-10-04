Ext.define('Nerif.component.indicator.CadastroIndicador', {
    extend: 'Ext.window.Window',

    modal: true,
    layout: 'fit',
    title: 'Cadastro de indicadores',
    
    width: 600,
    height: 400,
    
    initComponent: function () {
        var obj = this;

        var indicadoridHdn = Ext.create('Ext.form.Hidden', {
            name: 'id'
        });

        var regrasHdn = Ext.create('Ext.form.Hidden', {
        	name: 'regras'
        });
        
        var descricaoText = Ext.create('Ext.form.Text', {
        	name: 'descricao',
            allowBlank: false,
            fieldLabel: 'Descrição'
        });

        var adicionarRegraBtn = Ext.create('Ext.button.Button', {
            text: 'Nova regra',
            handler: function () {
                Ext.create('Nerif.component.indicator.CadastroRegra', {
                    listeners: {
                        'regrasalva': function (dados) {
                            regrasStore.add(dados);
                        }
                    }
                }).show();
            }
        });

        var regrasStore = Ext.create('Ext.data.Store', {
            fields: ['descPropriedade', 'infoPropriedade', 'tipoComparacao', 'tipoValor', 'valor1', 'valor2']
        });
        
        var regrasGrid = Ext.create('Ext.grid.Panel', {
        	height: 250,
            store: regrasStore,
            columns: [{
                xtype: 'templatecolumn',
                text: 'Regras',
                flex: 1,
                tpl: '{descPropriedade} - {infoPropriedade} - {tipoComparacao} - {tipoValor} - {valor1} - {valor2}'
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
            	
            	if(regrasStore.getCount()) {
            		regrasHdn.setValue(JSON.stringify(Ext.Array.pluck(regrasStore.getData().items, 'data')));
            	} else {
            		regras.setValue(null);
            	}
            	
                obj.fireEvent('indicadorsalvo', formpanel.getValues());
                obj.close();
            }
        });

        var formpanel = Ext.create('Ext.form.Panel', {
            bodyPadding: '5px',
            fieldDefaults: {
                labelAlign: 'right',
                width: '100%'
            },
            items: [indicadoridHdn, regrasHdn, descricaoText, regrasGrid],
            buttons: ['->', cancelarBtn, confirmarBtn]
        });

        this.items = [formpanel];
        
        this.callParent();
        
        this.editar = function(record) {
        	obj.show();
        	
        	formpanel.loadRecord(record);
        	if(record.data.regras) {
        		regrasStore.loadData(Ext.decode(record.data.regras));
        	}
        	
        	formpanel.isValid();
        };
        
        this.on('afterrender', function () {
            formpanel.isValid();
        });
    }
});