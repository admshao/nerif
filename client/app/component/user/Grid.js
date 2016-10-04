Ext.define('Nerif.component.user.Grid', {
	extend: 'Ext.grid.Panel',

	getNextId: function() {		
		return Ext.Array.max(Ext.Array.pluck(Gerenciador.users, 'id')) + 1 || 0;
	},

	store: Ext.create('Ext.data.Store', {
		fields: ['id', 'nome', 'email', 'telefone'],
		data: Gerenciador.users,
		listeners: {
			'datachanged': function(me, value) {
				Gerenciador.users = Ext.Array.pluck(me.getData().items, 'data');
			}
		}
	}),

	columns: [{
		xtype: 'templatecolumn',
		text: 'Usuários',
		flex: 1,
		tpl: Ext.create('Ext.XTemplate',
				'<strong><span>{nome}<span></strong>',
				'<br />',
				'<span style="font-size: 12px">Email: {email}</span>',
				'<tpl if="telefone">',
				'   <br />',
				'   <span style="font-size: 12px">Telefone: {telefone}</span>',
				'</tpl>'
		)
	}, {
		xtype: 'actioncolumn',
		width: 60,
		items: [{
			icon: 'images/edit.png',
			tooltip: 'Editar',
			handler: function (grid, rowIndex, colIndex) {
				var rec = grid.getStore().getAt(rowIndex);

				Ext.create('Nerif.component.user.Cadastro', {
					listeners: {
						'usuariosalvo': function (dados) {
							rec.set(dados);
						}
					}
				}).editar(rec);
			}
		}, {
			icon: 'images/delete.png',
			tooltip: 'Remover',
			handler: function (grid, rowIndex, colIndex) {
				var rec = grid.getStore().getAt(rowIndex);

				for(var i = 0; i < Gerenciador.groups.length; i++) {
					var group = Gerenciador.groups[i];
					for(var j = 0; j < group.users.length; j++) {
						var user = group.users[j];
						if(user.id === rec.data.id) {
							Ext.Msg.alert('Erro', 'Este usuário está vinculado com um ou mais grupos.');
							return false;
						}
					}
				}

				Ext.Msg.confirm('Atenção', 'Está ação irá excluir este usuário. Deseja continuar?', function(btn) {
					if(btn === 'yes') {
						grid.getStore().remove(rec);		
					}
				});  
			}
		}]
	}],

	buttons: ['->', {
		text: 'Adicionar',
		handler: function (me) {
			Ext.create('Nerif.component.user.Cadastro', {
				listeners: {
					'usuariosalvo': function (dados) {
						var grid = me.up('grid');

						dados.id = grid.getNextId();
						grid.getStore().add(dados);
					}
				}
			}).show();
		}
	}]
});