Ext.define('Nerif.component.grupo.Grid', {
    extend: 'Ext.grid.Panel',

    viewModel: {
        type: 'gerenciador'
    },

    bind: {
        store: '{groups}'
    },

    //todo algum controle de id
    currentId: 0,

    cadastroWindow: null,
    abrirCadastroWindow: function (record) {
	var obj = this;

        if (!this.cadastroWindow) {
            this.cadastroWindow = Ext.create('Nerif.component.grupo.Cadastro', {
                listeners: {
                    'gruposalvo': function (dados) {
                        if (record) {
                            record.set(dados);
                        } else {
                            dados.id = obj.currentId++;
                            obj.getStore().add(dados);
                        }
                    }
                }
            });
        }

        if (record) {
            this.cadastroWindow.editar(record);
        } else {
            this.cadastroWindow.show();
        }
    },

    columns: [{
        xtype: 'templatecolumn',
        text: 'Grupos',
        flex: 1,
        tpl: '{id} - {descricao} - {users} - {indicators}'
    }],

    buttons: ['->', {
        text: 'Adicionar',
        handler: function (me) {
            me.up('grid').abrirCadastroWindow();
        }
    }]
});