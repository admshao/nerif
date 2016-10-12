Ext.define('Nerif.component.indicator.CadastroRegra', {
	extend: 'Ext.window.Window',

	modal: true,
	layout: 'fit',
	title: 'Cadastro de regras',

	initComponent: function () {
		var obj = this;

		var getTipoComparacaoByTipoValor = function(tipoValor) {
			switch(tipoValor) {
				case 'NUMERICO':
				case 'PERCENTUAL':
				case 'DATA':
				case 'HORA':
					return [['IGUAL'], ['DIFERENTE'], ['MAIOR_QUE'], ['MENOR_QUE'], ['NO_INTERVALO'], ['FORA_DO_INTERVALO']];
				case 'STRING':
				case 'BOOLEAN':
					return [['IGUAL'], ['DIFERENTE']];
				default:
					return [];
			}
		};

		var createNumericoFields = function(tipoComparacao) {
			var valor1Field = Ext.create('Ext.form.Number', {
				name: 'valor1',
				allowBlank: false
			});
			
			valoresContainer.add(valor1Field);
			
			if(tipoComparacao === 'NO_INTERVALO' || tipoComparacao === 'FORA_DO_INTERVALO') {
				var valor2Field = Ext.create('Ext.form.Number', {
					name: 'valor2',
					allowBlank: false
				});
				
				valoresContainer.add(valor2Field);
			}
		};
		
		var createDataFields = function(tipoComparacao) {
			var valor1Field = Ext.create('Ext.form.Date', {
				name: 'valor1',
				allowBlank: false
			});
			
			valoresContainer.add(valor1Field);
			
			if(tipoComparacao === 'NO_INTERVALO' || tipoComparacao === 'FORA_DO_INTERVALO') {
				var valor2Field = Ext.create('Ext.form.Date', {
					name: 'valor2',
					allowBlank: false
				});
				
				valoresContainer.add(valor2Field);
			}
		};
		
		var createHoraFields = function(tipoComparacao) {
			var valor1Field = Ext.create('Ext.form.Time', {
				name: 'valor1',
				allowBlank: false
			});
			
			valoresContainer.add(valor1Field);
			
			if(tipoComparacao === 'NO_INTERVALO' || tipoComparacao === 'FORA_DO_INTERVALO') {
				var valor2Field = Ext.create('Ext.form.Time', {
					name: 'valor2',
					allowBlank: false
				});
				
				valoresContainer.add(valor2Field);
			}	
		};
		
		var createStringFields = function(tipoComparacao) {
			var valor1Field = Ext.create('Ext.form.Text', {
				name: 'valor1',
				allowBlank: false
			});
			
			valoresContainer.add(valor1Field);
		};
		
		var createBooleanFields = function(tipoComparacao) {
			var valor1Field = Ext.create('Ext.form.CheckBox', {
				name: 'valor1'
			});
			
			valoresContainer.add(valor1Field);
		};

		var createValueFields = function() {
			valoresContainer.removeAll(true);

			var tipoValor = tipoValorHdn.getValue();
			var tipoComparacao = tipoComparacaoCombo.getValue();

			if(tipoValor && tipoComparacao) {
				switch(tipoValor) {
				case 'NUMERICO':
				case 'PERCENTUAL':
					createNumericoFields(tipoComparacao);
					break;
				case 'DATA':
					createDataFields(tipoComparacao);
					break;
				case 'HORA':
					createHoraFields(tipoComparacao);
					break;
				case 'STRING':
					createStringFields(tipoComparacao);
					break;
				case 'BOOLEAN':
					createBooleanFields(tipoComparacao);
					break;
				}
			}
		};

		var descPropriedadeHdn = Ext.create('Ext.form.Hidden', {
			name: 'descPropriedade'
		});

		var tipoValorHdn = Ext.create('Ext.form.Hidden', {
			name: 'tipoValor'
		});

		var propriedadeStore = Ext.create('Ext.data.Store', {
			fields: ['description', 'infoPropriedade', 'tipoValor'],
			data: Gerenciador.logProperties
		});

		var propriedadeCombo = Ext.create('Ext.form.ComboBox', {
			name: 'infoPropriedade',
			allowBlank: false,
			editable: false,
			store: propriedadeStore,
			valueField: 'infoPropriedade',
			displayField: 'description',
			fieldLabel: 'Propriedade',
			listeners: {
				'change': function(me, value) {
					var rec = me.findRecordByValue(value);

					descPropriedadeHdn.setValue(rec.data.description);
					tipoValorHdn.setValue(rec.data.tipoValor);					
					
					var tipoComparacaoArray = getTipoComparacaoByTipoValor(rec.data.tipoValor);
					tipoComparacaoStore.loadData(tipoComparacaoArray);
					tipoComparacaoCombo.setValue(null);
				}
			}
		});

		var tipoComparacaoStore = Ext.create('Ext.data.ArrayStore', {
			fields: ['tipoComparacao']
		});

		var tipoComparacaoCombo = Ext.create('Ext.form.ComboBox', {
			name: 'tipoComparacao',
			allowBlank: false,
			editable: false,
			store: tipoComparacaoStore,
			queryMode: 'local',
			valueField: 'tipoComparacao',
			displayField: 'tipoComparacao',
			fieldLabel: 'Tipo de comparação',
			listeners: {
				'change': function(me, value) {					
					createValueFields();
				}
			}
		});

		var valoresContainer = Ext.create('Ext.form.FieldContainer');

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
			items: [descPropriedadeHdn, tipoValorHdn, propriedadeCombo, tipoComparacaoCombo, valoresContainer],
			buttons: ['->', cancelarBtn, confirmarBtn]
		});

		this.items = [formpanel];

		this.callParent();
		
		this.editar = function(record) {
			obj.show();
			
			formpanel.loadRecord(record);
			formpanel.isValid();
		};
		
		this.on('afterrender', function() {
			formpanel.isValid();	
		});
	}
});