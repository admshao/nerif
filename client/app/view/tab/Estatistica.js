var fs = require('fs');
var path = require('path');

Ext.define('Nerif.view.tab.Estatistica', {
    extend: 'Ext.form.Panel',

    title: 'Estatísticas',
    layout: 'border',
    bodyPadding: '10px',
    
    initComponent: function() {
    	var obj = this;
    	
    	var lastModifiedTime = null;
    	var statisticData = null;
    	
    	var totalRequisicoesHora = function(dados, filtro) {
    		
    		var reqHora = {};
    		
    		for(var key in dados.requisicoes) {
    			if(filtro && key.indexOf(filtro) === -1)
    				continue;
    			
    			var horarios = dados.requisicoes[key].horarios;
    			
    			for(var key2 in horarios) {
    				
    				var dt = Ext.Date.format(Ext.Date.parse(key2, 'H:i:s'), 'G');
    				    				
    				if(!reqHora[dt]) {
    					reqHora[dt] = 1;
    				} else {
    					reqHora[dt]++;
    				}    				
    			}    			
    		}
    		    		
    		var data = [];    		
    		for(var i = 0; i < 24; i++) {
    			
				data.push({
    				horario: Ext.Date.format(Ext.Date.parse(i, 'G'), 'H:i'),
    				quantidade: reqHora[i] ? reqHora[i] : 0
    			});
    			
    		}
    		
    		centerPanel.add({    			
    			xtype: 'cartesian',
    			insetPadding: 40,
				store: {
					fields: ['horario', 'quantidade'],
					data: data
				},
				axes: [{
					type: 'numeric',
					position: 'left',
					fields: ['quantidade'],
					grid: true,
					minimum: 0
				}, {
					type: 'category',
					position: 'bottom',
					fields: ['horario']
				}],
				series: [{
					type: 'bar',
					xField: 'horario',
					yField: 'quantidade',
					subStyle: {
						fill: ['#5fa2dd'],
						stroke: '#5fa2dd'
					},
					tooltip: {
						trackMouse: true,
						width: 175,
						renderer: function (toolTip, record, ctx) {
							toolTip.setHtml(record.get('horario') + ': ' + record.get('quantidade') + ' requisições');
						}
					}
				}]
    		});
    	};
    	
    	var totalExtensoes = function(dados, filtro) {
    		
    		var extQtd = {};
    		
    		for(var key in dados.requisicoes) {    			
    			var extensao = dados.requisicoes[key].extensao;
    			
    			if(!extensao || (filtro && extensao !== filtro))
    				continue;
    			
    			var quantidade = dados.requisicoes[key].quantidade;
    			if(!extQtd[extensao]) {
    				extQtd[extensao] = quantidade;
    			} else {
    				extQtd[extensao] += quantidade;
    			}
    		}
    		
    		var data = [];
    		for(var key in extQtd) {
    			data.push({
    				extensao: key,
    				quantidade: extQtd[key] 
    			});
    		}
    		
    		centerPanel.add({    			
    			xtype: 'cartesian',
    			insetPadding: 40,
				store: {
					fields: ['extensao', 'quantidade'],
					data: data
				},
				axes: [{
					type: 'numeric',
					position: 'left',
					fields: ['quantidade'],
					grid: true,
					minimum: 0
				}, {
					type: 'category',
					position: 'bottom',
					fields: ['extensao']
				}],
				series: [{
					type: 'bar',
					xField: 'extensao',
					yField: 'quantidade',
					selectionTolerance: 5,
					tooltip: {
						trackMouse: true,
						width: 175,
						renderer: function (toolTip, record, ctx) {
							toolTip.setHtml(record.get('extensao') + ': ' + record.get('quantidade') + ' requisições');
						}
					}
				}]
    		});
    	};
    	
    	var totalIP = function(dados, filtro) {
    		
    		var ipQtd = {};
    		
    		for(var key in dados.requisicoes) {
    			var horarios = dados.requisicoes[key].horarios;
    			for(var horario in horarios) {
    				var ips = Ext.Array.pluck(horarios[horario].linhas, 'ipOrigem');
    			
    				for(var i = 0; i < ips.length; i++) {
    					var ip = ips[i];
    					
    					if(filtro && ip.indexOf(filtro) === -1)
    						continue;
    					
    					if(!ipQtd[ip]) {
    						ipQtd[ip] = 1;
        				} else {
        					ipQtd[ip]++;
        				}    					
    				}    				
    			}    			
    		}
    		
    		var data = [];
    		for(var key in ipQtd) {
    			data.push({
    				ip: key,
    				quantidade: ipQtd[key]
    			})
    		}
    		
    		centerPanel.add({    			
    			xtype: 'cartesian',
    			insetPadding: 40,
				store: {
					fields: ['ip', 'quantidade'],
					data: data
				},
				axes: [{
					type: 'numeric',
					position: 'left',
					fields: ['quantidade'],
					grid: true,
					minimum: 0
				}, {
					type: 'category',
					position: 'bottom',
					fields: ['ip']
				}],
				series: [{
					type: 'bar',
					xField: 'ip',
					yField: 'quantidade',
					subStyle: {
						fill: ['#5fa2dd'],
						stroke: '#5fa2dd'
					},
					tooltip: {
						trackMouse: true,
						width: 200,
						renderer: function (toolTip, record, ctx) {
							toolTip.setHtml(record.get('ip') + ': ' + record.get('quantidade') + ' requisições');
						}
					}
				}]
    		});
    		
    	};
    	
    	var totalBytesHora = function(dados, filtro) {
    		
    		var bytesHora = {};
    		
    		for(var key in dados.requisicoes) {
    			if(filtro && key.indexOf(filtro) === -1)
    				continue;
    			
    			var horarios = dados.requisicoes[key].horarios;
    			
    			for(var horario in horarios) {    				
    				var dt = Ext.Date.format(Ext.Date.parse(horarios[horario], 'H:i:s'), 'G');
    				    				
    				var tamanho = Ext.Array.sum(Ext.Array.pluck(horarios[horario].linhas, 'tamanho'));
    				
    				if(!bytesHora[dt]) {
    					bytesHora[dt] = tamanho;
    				} else {
    					bytesHora[dt] += tamanho;
    				}    				
    			}    			
    		}
    		
    		var data = [];    		
    		for(var i = 0; i < 24; i++) {
    			
				data.push({
    				horario: Ext.Date.format(Ext.Date.parse(i, 'G'), 'H:i'),
    				totalBytes: bytesHora[i] ? bytesHora[i] : 0
    			});
    			
    		}
    		
    		centerPanel.add({    			
    			xtype: 'cartesian',
    			insetPadding: 40,
				store: {
					fields: ['horario', 'totalBytes'],
					data: data
				},
				axes: [{
					type: 'numeric',
					position: 'left',
					fields: ['totalBytes'],
					grid: true,
					minimum: 0
				}, {
					type: 'category',
					position: 'bottom',
					fields: ['horario']
				}],
				series: [{
					type: 'bar',
					xField: 'horario',
					yField: 'totalBytes',
					subStyle: {
						fill: ['#5fa2dd'],
						stroke: '#5fa2dd'
					},
					tooltip: {
						trackMouse: true,
						width: 175,
						renderer: function (toolTip, record, ctx) {
							toolTip.setHtml(record.get('horario') + ': ' + record.get('totalBytes') + ' bytes');
						}
					}
				}]
    		});
    		
    	};
    	
    	var tempoPorUrlPorHora = function(dados, filtro) {
    		
    		var data = [];
    		
    		for(var key in dados.requisicoes) {
    			if(filtro && key.indexOf(filtro) === -1)
    				continue;
    			
    			var tempoMin = dados.requisicoes[key].tempoMin;
    			var tempoMax = dados.requisicoes[key].tempoMax;
    			
    			var horarios = dados.requisicoes[key].horarios;    			   			
    			for(var horario in horarios) {
    				
    				var tempoMedio = Ext.Array.sum(Ext.Array.pluck(horarios[horario].linhas, 'duracao')) / horarios[horario].linhas.length;
    				
    				data.push({
    					url: key,
    					horario: horario,
    					tempoMin: tempoMin,
    					tempoMax: tempoMax,
    					tempoMedio: tempoMedio
    				});
    				
    			}    			
    		}
    		
    		centerPanel.add({    			
    			xtype: 'cartesian',
    			insetPadding: 40,
				store: {
					fields: ['url', 'horario', 'tempoMin', 'tempoMax', 'tempoMedio'],
					data: data
				},
				axes: [{
					type: 'numeric',
					position: 'left',
					fields: ['tempoMin', 'tempoMax', 'tempoMedio'],
					grid: true,
					minimum: 0
				}, {
					type: 'category',
					position: 'bottom',
					fields: ['horario']
				}],
				series: [{
					type: 'line',
					style: {
						stroke: 'red',
						lineWidth: 2
					},
					xField: 'horario',
					yField: 'tempoMax'
				}, {
					type: 'line',
					style: {
						stroke: 'green',
						lineWidth: 2
					},
					xField: 'horario',
					yField: 'tempoMin'
				}, {
					type: 'line',
					style: {
						stroke: 'blue',
						lineWidth: 2
					},
					xField: 'horario',
					yField: 'tempoMedio',
                    selectionTolerance: 5,
					tooltip: {
						width: 175,					
						trackMouse: true,						
						renderer: function (toolTip, record, ctx) {
							toolTip.setHtml('Tempo médio de requisições em ' + record.get('horario') + ': ' + record.get('tempoMedio'));
						}
					}
				}]
    		});
    		
    	};
    	
    	var gerarGrafico = function() {
    		var data = datasComEstatisticaCombo.getValue();
    		var opcao = opcoesCombo.getValue();
    		var filtro = filtroText.getValue();
    		    		
    		centerPanel.removeAll(true);
    		centerPanel.update('');
    		
    		if(!data || !opcao) {
    			centerPanel.update('<center><div><strong>Selecione uma data e uma opção<strong></div></center>');
    		} else {
    			
    			if (statisticData) {    					
					eval(opcao)(statisticData, filtro);  								
    					
        		} else {
        			centerPanel.update('<center><div><strong>Não existem dados para a data escolhida<strong></div></center>');	
        		}    			
    		}
    	};
    	
    	var datasComEstatisticaStore = Ext.create('Ext.data.Store', {
    		fields: [
		         { name: 'data', type: 'date' },
		         { name: 'dataFormatada' }
	         ],
	         sorters: [{
	        	 'property': 'data',
	        	 'direction': 'ASC'
	         }]
    	});
    	
    	var datasComEstatisticaCombo = Ext.create('Ext.form.ComboBox', {
    		fieldLabel: 'Data',
    	    labelAlign: 'right',
    		store: datasComEstatisticaStore,
    		valueField: 'data',
    		displayField: 'dataFormatada',
    		editable: false,
    		queryMode: 'local',
    		allowBlank: false,
    		listeners: {
    			'change': function(me, value) {
    				if(!value) {
    					lastModifiedTime = null;
    					statisticData = null;
    				} else {
    				
	    				var path = './statistics/'+ Ext.Date.format(value, 'Y-m-d') + '.json';
	    				if (fs.existsSync(path)) {
	    					
	    					var stat = fs.statSync(path);	    					
	    					if(lastModifiedTime && lastModifiedTime === stat.mtime) {
	    						return;
	    					}
	    					
	    					lastModifiedTime = stat.mtime;
	    					
	    					fs.readFile(path, "utf8", function (err, dados) {
	    						statisticData = Ext.decode(dados);
	    					});	    					
	    				}
    				}
    			}
    		}
    	});
    	
    	var opcoesStore = Ext.create('Ext.data.Store', {
    	    fields: ['valor', 'descricao'],
    	    data : [
    	        { "valor": "totalRequisicoesHora", "descricao": "Total de requisições por hora" },
    	        { "valor": "totalExtensoes", "descricao": "Total de requisições por extensão de arquivo" },
    	        { "valor": "totalIP", "descricao": "Total de requisições por IP" },
    	        { "valor": "totalBytesHora", "descricao": "Total de bytes por hora" },
    	        { "valor": "tempoPorUrlPorHora", "descricao": "Tempo médio de requisições por URL por hora" }    	        
    	    ]
    	});

    	var opcoesCombo = Ext.create('Ext.form.ComboBox', {
    	    fieldLabel: 'Opções',
    	    labelAlign: 'right',
    	    width: 450,
    	    store: opcoesStore,
    	    queryMode: 'local',
    	    editable: false,
    	    displayField: 'descricao',
    	    valueField: 'valor',
    		allowBlank: false,
    		listeners: {
    			'select': function(me) {
    				
    				filtroText.show();
    				    				
    				filtroText.allowBlank = true;    				
    				filtroText.setValue(null);
    				
    				switch(me.getValue()) {
    				case 'totalRequisicoesHora':
    				case 'totalBytesHora':
    					filtroText.setFieldLabel('URL');
    					break;
    				case 'tempoPorUrlPorHora':
    					filtroText.setFieldLabel('URL');
    					filtroText.allowBlank = false;
    					break;
    				case 'totalExtensoes':
    					filtroText.setFieldLabel('Extensão');
    					break;
    				case 'totalIP':
    					filtroText.setFieldLabel('IP');
    					break;
					default:
						filtroText.setFieldLabel('Teste');
    				}
    				
    				
    				filtroText.validate();
    			}
    		}
    	});
    	
    	var filtroText = Ext.create('Ext.form.Text', {
    		labelAlign: 'right',
    		width: 300,
    		hidden: true
    	});
    	
    	var buscarBtn = Ext.create('Ext.button.Button', {
    		formBind: true,
    		text: 'Buscar',
    		margin: '0 0 0 20px',
    		handler: function() {
    			gerarGrafico();
    		}
    	});
    	
    	var opcoesPanel = Ext.create('Ext.form.Panel', {
    		items: [datasComEstatisticaCombo, opcoesCombo, filtroText, buscarBtn],
    		layout: 'hbox',
    		region: 'north',
    		height: 40    		
    	});
    	
    	var centerPanel = Ext.create('Ext.panel.Panel', {
    		region: 'center',
    		layout: 'fit'
    	});
    	
    	this.items = [opcoesPanel, centerPanel];
    	
    	this.on('activate', function() {
    		   		
    		datasComEstatisticaStore.removeAll(true);
    		datasComEstatisticaCombo.setValue(null);
    		opcoesCombo.setValue(null);
    		filtroText.setValue(null);
    		
    		opcoesPanel.isValid();
    		
    		if(fs.existsSync('./statistics/')) {    			
    			
    			fs.readdir('./statistics/', (err, files) => {
    				var datas = [];
    				files.forEach(file => {
    					var dataStr = path.basename(file, path.extname(file));
    					var data = Ext.Date.parse(dataStr, 'Y-m-d');
    					
    					datas.push({ 
    						data: data,
    						dataFormatada: Ext.Date.format(data, 'd/m/Y')
						});
    				});
    				
    				datasComEstatisticaStore.loadData(datas);
    			})
    			
    		}
    		
    		gerarGrafico();
    	});
    	
    	this.callParent();
    }
});