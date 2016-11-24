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
                tpl: Ext.create('Ext.XTemplate',
                	'<span>{[this.getDescricaoRegra(values)]}</span>',
                	{
                		getDescricaoRegra: function(values) {
                			var desc = values.descPropriedade;
                			
                			var valor1 = values.valor1;
                			var valor2 = values.valor2;
                			
                			if(values.tipoValor === 'DATA') {
                				
                				if(valor1)
                					valor1 = Ext.Date.format(Ext.Date.parse(valor1, 'Y-m-d'), 'd/m/Y');
                				
                				if(valor2)
                					valor2 = Ext.Date.format(Ext.Date.parse(valor2, 'Y-m-d'), 'd/m/Y');
                			}
                			
                			switch(values.tipoComparacao) {
                				case 'IGUAL':
	                				desc += ' igual a ' + valor1;
	                				break;
                				case 'DIFERENTE':
	                				desc += ' diferente de ' + valor1;
	                				break;
                				case 'MAIOR_QUE':
	                				desc += ' maior que ' + valor1;
	                				break;
                				case 'MENOR_QUE':
	                				desc += ' menor que ' + valor1;
	                				break;
                				case 'NO_INTERVALO':
	                				desc += ' entre ' + valor1 + ' e ' + valor2;
	                				break;
                				case 'FORA_DO_INTERVALO':
	                				desc += ' fora do intervalo de ' + valor1 + ' a ' + valor2;
	                				break;
                			}
                			
                			return desc;
                		}
                	}
        		) 
            }, {
        		xtype: 'actioncolumn',
        		width: 60,
        		items: [{
        			iconCls: 'edit',
        			tooltip: 'Editar',
        			handler: function (grid, rowIndex, colIndex) {
        				var rec = grid.getStore().getAt(rowIndex);

        				Ext.create('Nerif.component.indicator.CadastroRegra', {
        					listeners: {
        						'regrasalva': function (dados) {
        							rec.set(dados);
        						}
        					}
        				}).editar(rec);
        			}
        		}, {
        			iconCls: 'delete',
        			tooltip: 'Remover',
        			handler: function (grid, rowIndex, colIndex) {
        				var rec = grid.getStore().getAt(rowIndex);

        				Ext.Msg.confirm('Atenção', 'Deseja mesmo remover está regra?', function(btn) {
        					if(btn === 'yes') {
        						grid.getStore().remove(rec);		
        					}
        				});  
        			}
        		}]
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
            		regrasHdn.setValue(null);
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