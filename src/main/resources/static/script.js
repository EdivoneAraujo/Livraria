// ==========================================
// CONFIGURAÇÃO GLOBAL DA API
// ==========================================
const API_BASE_URL = 'http://localhost:8080/api';

// Variável global para gerir os itens no carrinho da loja
let carrinho = [];

// ==========================================
// FUNÇÃO AUXILIAR: EXTRAÇÃO SEGURA DOS AUTORES
// ==========================================
function obterNomeAutor(livroOuAutor) {
    if (!livroOuAutor) return 'Autor desconhecido';
    if (typeof livroOuAutor === 'string') return livroOuAutor;

    // Caso receba o objeto Livro completo com o array Set<Autor> 'autores'
    if (Array.isArray(livroOuAutor.autores) && livroOuAutor.autores.length > 0) {
        return livroOuAutor.autores.map(a => a.nome || a.nomeAutor || 'Autor').join(', ');
    }

    // Compatibilidade caso venha objeto Autor isolado
    if (livroOuAutor.autor) {
        return obterNomeAutor(livroOuAutor.autor);
    }

    return livroOuAutor.nome || livroOuAutor.nomeAutor || 'Autor desconhecido';
}

// ==========================================
// CONTROLO DE SESSÃO E ESTADO DO TOPO
// ==========================================
function verificarEstadoSessao() {
    const cpfCliente = localStorage.getItem('clienteLogadoCPF');
    const nomeCliente = localStorage.getItem('clienteLogadoNome') || 'Cliente Tríade';
    const cpfVendedor = localStorage.getItem('vendedorLogadoCPF');
    const adminEmail = localStorage.getItem('adminLogadoEmail');

    const menuDeslogado = document.getElementById('menu-deslogado');
    const menuCliente = document.getElementById('menu-cliente-logado');
    const menuVendedor = document.getElementById('menu-vendedor-logado');
    const menuAdminLogado = document.getElementById('menu-admin-logado');

    if (menuDeslogado) menuDeslogado.classList.add('oculto');
    if (menuCliente) menuCliente.classList.add('oculto');
    if (menuVendedor) menuVendedor.classList.add('oculto');
    if (menuAdminLogado) menuAdminLogado.classList.add('oculto');

    if (cpfCliente) {
        if (menuCliente) menuCliente.classList.remove('oculto');
        const spanCliente = document.getElementById('nome-cliente-topo');
        if (spanCliente) {
            spanCliente.textContent = `👤 ${nomeCliente}`;
        }
    } else if (cpfVendedor) {
        if (menuVendedor) menuVendedor.classList.remove('oculto');
        const spanVendedor = document.getElementById('nome-vendedor-topo');
        if (spanVendedor) {
            spanVendedor.textContent = `👤 Vendedor (${cpfVendedor})`;
        }
    } else if (adminEmail) {
        if (menuAdminLogado) menuAdminLogado.classList.remove('oculto');
        const spanAdmin = document.getElementById('nome-admin-topo');
        if (spanAdmin) {
            spanAdmin.textContent = `👤 ${adminEmail}`;
        }
    } else {
        if (menuDeslogado) menuDeslogado.classList.remove('oculto');
    }
}

function atualizarModoCabecalho(modo) {
    const navLoja = document.getElementById('nav-mode-loja');
    const navAdmin = document.getElementById('nav-mode-admin');
    const navCliente = document.getElementById('nav-mode-cliente');

    const buscaLoja = document.getElementById('busca-topo-loja');
    const carrinhoBtn = document.getElementById('cart-icon');

    if (navLoja) navLoja.classList.add('oculto');
    if (navAdmin) navAdmin.classList.add('oculto');
    if (navCliente) navCliente.classList.add('oculto');

    if (buscaLoja) buscaLoja.style.display = 'flex';
    if (carrinhoBtn) carrinhoBtn.style.display = 'flex';

    if (modo === 'admin') {
        if (navAdmin) navAdmin.classList.remove('oculto');
        if (buscaLoja) buscaLoja.style.display = 'none';
        if (carrinhoBtn) carrinhoBtn.style.display = 'none';
    } else if (modo === 'cliente-historico') {
        if (navCliente) navCliente.classList.remove('oculto');
        if (buscaLoja) buscaLoja.style.display = 'none';
        if (carrinhoBtn) carrinhoBtn.style.display = 'none';
    } else {
        if (navLoja) navLoja.classList.remove('oculto');
    }

    verificarEstadoSessao();
}

window.addEventListener('DOMContentLoaded', () => {
    verificarEstadoSessao();
    carregarVitrine();
});

// ==========================================
// ROTEAMENTO E CONTROLO DE TELAS (ISOLAMENTO)
// ==========================================
function ocultarTodosPainels() {
    const paineis = [
        'loja-sistema',
        'app-sistema',
        'app-cliente',
        'app-vendedor-vendas',
        'login-screen',
        'cliente-login-screen',
        'vendedor-login-screen',
        'tela-escolha-login'
    ];

    paineis.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.classList.add('oculto');
    });
}

function voltarParaLoja(event) {
    if (event) event.preventDefault();
    ocultarTodosPainels();

    const loja = document.getElementById('loja-sistema');
    if (loja) loja.classList.remove('oculto');

    atualizarModoCabecalho('loja');
    carregarVitrine();
}

function escolherPerfil(perfil) {
    fecharTelaEscolhaLogin();
    ocultarTodosPainels();

    if (perfil === 'admin') {
        const adminLogin = document.getElementById('login-screen');
        if (adminLogin) adminLogin.classList.remove('oculto');
    } else if (perfil === 'vendedor') {
        const vendedorLogin = document.getElementById('vendedor-login-screen');
        if (vendedorLogin) vendedorLogin.classList.remove('oculto');
    } else if (perfil === 'cliente') {
        const clienteLogin = document.getElementById('cliente-login-screen');
        if (clienteLogin) clienteLogin.classList.remove('oculto');
    } else {
        const loja = document.getElementById('loja-sistema');
        if (loja) loja.classList.remove('oculto');
        atualizarModoCabecalho('loja');
        carregarVitrine();
    }
}

// ==========================================
// MODAL "SOBRE NÓS"
// ==========================================
function abrirModalSobre(event) {
    if (event) event.preventDefault();
    const modal = document.getElementById('modal-sobre');
    if (modal) modal.classList.remove('oculto');
}

function fecharModalSobre() {
    const modal = document.getElementById('modal-sobre');
    if (modal) modal.classList.add('oculto');
}

// ==========================================
// CONTROLO DE AUTENTICAÇÃO E LOGIN (ADMIN)
// ==========================================
async function realizarLogin(event) {
    event.preventDefault();

    const emailInput = document.getElementById('email');
    const senhaInput = document.getElementById('senha');

    const email = emailInput ? emailInput.value.trim() : '';
    const senha = senhaInput ? senhaInput.value.trim() : '';

    if (!email || !senha) {
        alert("Por favor, preencha o e-mail e a palavra-passe.");
        return;
    }

    const botaoEntrar = document.querySelector('#form-login button[type="submit"]');
    if (botaoEntrar) botaoEntrar.textContent = "A validar...";

    try {
        const resposta = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ identificador: email, email, senha })
        });

        const dados = await resposta.json();

        if (resposta.ok && dados.sucesso) {
            localStorage.setItem('adminLogadoEmail', email);
            if (dados.idPessoa) localStorage.setItem('adminLogadoId', dados.idPessoa);

            ocultarTodosPainels();
            const appSistema = document.getElementById('app-sistema');
            if (appSistema) appSistema.classList.remove('oculto');

            atualizarModoCabecalho('admin');
            if (botaoEntrar) botaoEntrar.textContent = "Entrar no Sistema";

            carregarAcervoAdmin();
            atualizarMetricas();
            mostrarAba('livros');
        } else {
            alert(dados.mensagem || "E-mail ou palavra-passe incorretos.");
            if (botaoEntrar) botaoEntrar.textContent = "Entrar no Sistema";
        }
    } catch (erro) {
        console.error("Erro ao comunicar com o servidor de autenticação:", erro);
        alert("Não foi possível conectar ao servidor. Verifique se o backend está ligado na porta 8080.");
        if (botaoEntrar) botaoEntrar.textContent = "Entrar no Sistema";
    }
}

function realizarLogout() {
    localStorage.removeItem('adminLogadoEmail');
    localStorage.removeItem('adminLogadoId');
    voltarParaLoja();
}

function logoutGeral() {
    localStorage.removeItem('clienteLogadoId');
    localStorage.removeItem('clienteLogadoCPF');
    localStorage.removeItem('clienteLogadoNome');
    localStorage.removeItem('clienteLogadoEmail');
    localStorage.removeItem('clienteLogadoTelefone');
    localStorage.removeItem('clienteLogadoEndereco');

    localStorage.removeItem('vendedorLogadoCPF');
    localStorage.removeItem('vendedorLogadoNome');
    localStorage.removeItem('adminLogadoEmail');
    localStorage.removeItem('adminLogadoId');

    voltarParaLoja();
    alert("Sessão terminada com sucesso.");
}

// ==========================================
// ROTEAMENTO: PORTAL DE ACESSO (MODAL)
// ==========================================
function abrirTelaEscolhaLogin() {
    const modal = document.getElementById('tela-escolha-login');
    if (modal) modal.classList.remove('oculto');
}

function fecharTelaEscolhaLogin() {
    const modal = document.getElementById('tela-escolha-login');
    if (modal) modal.classList.add('oculto');
}

// ==========================================
// FUNÇÕES DO PAINEL ADMIN (ABAS)
// ==========================================
function mostrarAba(abaId, event) {
    if (event) event.preventDefault();

    document.querySelectorAll('.aba-conteudo').forEach(el => {
        el.classList.add('oculto');
        el.classList.remove('active');
    });

    document.querySelectorAll('.admin-header .nav-menu a, nav.nav-menu .nav-subgrupo a').forEach(el => {
        if (!el.classList.contains('btn-sair')) {
            el.classList.remove('ativo');
            el.style.color = '';
        }
    });

    const abaSelecionada = document.getElementById(abaId);
    if (abaSelecionada) {
        abaSelecionada.classList.remove('oculto');
        abaSelecionada.classList.add('active');
    }

    const linkMenu = document.getElementById('nav-' + abaId);
    if (linkMenu) {
        linkMenu.classList.add('ativo');
        linkMenu.style.color = 'var(--cor-destaque)';
    }

    if (abaId === 'relatorios') {
        atualizarMetricas();
    } else if (abaId === 'livros') {
        carregarAcervoAdmin();
    } else if (abaId === 'faturas') {
        pesquisarFaturas();
    }
}

async function cadastrarLivroAdmin(event) {
    event.preventDefault();

    const autoresInput = document.getElementById('livro-autor').value.trim();
    const listaAutores = autoresInput.split(',')
        .map(nome => nome.trim())
        .filter(nome => nome.length > 0)
        .map(nome => ({ nome }));

    const livroDTO = {
        titulo: document.getElementById('livro-titulo').value.trim(),
        autores: listaAutores,
        preco: parseFloat(document.getElementById('livro-preco').value) || 0.0,
        quantidadeEstoque: parseInt(document.getElementById('livro-estoque').value) || 0,
        tematica: document.getElementById('livro-tematica').value.trim(),
        imagem: document.getElementById('livro-imagem').value.trim() || 'img/capa.png'
    };

    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(livroDTO)
        });

        if (resposta.ok) {
            alert('📚 Livro registado com sucesso no acervo!');
            document.getElementById('form-cad-livro').reset();
            carregarAcervoAdmin();
        } else {
            const erroMsg = await resposta.text();
            alert('Erro ao registar livro: ' + erroMsg);
        }
    } catch (e) {
        console.error("Erro de conexão:", e);
        alert("Não foi possível conectar ao servidor para registar o livro.");
    }
}

async function carregarAcervoAdmin() {
    const container = document.getElementById('lista-acervo-admin');
    if (!container) return;

    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`);
        if (!resposta.ok) return;

        const livros = await resposta.json();
        container.innerHTML = '';

        if (livros.length === 0) {
            container.innerHTML = '<p class="msg-vazia">Nenhum livro registado no acervo.</p>';
            return;
        }

        livros.forEach(livro => {
            const item = document.createElement('div');
            item.className = 'item-lista-admin';

            const autorNome = obterNomeAutor(livro);
            const estoqueBaixo = livro.alertaEstoque || livro.quantidadeEstoque <= (livro.estoqueMinimo || 5);
            const alertaEstoque = estoqueBaixo ? '⚠️ ' : '';
            const classeEstoque = estoqueBaixo ? 'estoque-baixo' : 'estoque-normal';

            item.innerHTML = `
                <div>
                    <strong class="item-titulo-admin">${livro.titulo}</strong><br>
                    <span class="item-sub-admin">Autor(es): ${autorNome} | Preço: R$ ${livro.preco.toFixed(2)}</span><br>
                    <span class="item-estoque-admin ${classeEstoque}">${alertaEstoque}Estoque: ${livro.quantidadeEstoque} unid. (Mín: ${livro.estoqueMinimo || 5})</span>
                </div>
            `;
            container.appendChild(item);
        });
    } catch (e) {
        container.innerHTML = '<p class="msg-erro">Erro ao carregar o acervo.</p>';
    }
}

async function atualizarMetricas() {
    let totalLivros = 0;
    let totalVendas = 0;
    let faturamentoTotal = 0;
    let produtoMaisVendido = "Sem dados";
    let melhorCliente = "Sem dados";
    let textoAlertaEstoque = "";

    try {
        const respostaLivros = await fetch(`${API_BASE_URL}/livros`);
        if (respostaLivros.ok) {
            const livros = await respostaLivros.json();
            totalLivros = livros.length;

            const livrosBaixoEstoque = livros.filter(livro => livro.alertaEstoque || livro.quantidadeEstoque <= (livro.estoqueMinimo || 5));

            if (livrosBaixoEstoque.length > 0) {
                textoAlertaEstoque = "\n\n=== ALERTAS DE ESTOQUE (Mínimo Atingido) ===\n";
                livrosBaixoEstoque.forEach(livro => {
                    textoAlertaEstoque += `⚠️ ${livro.titulo} - Restam apenas ${livro.quantidadeEstoque} unid.\n`;
                });
            }
        }
    } catch (e) {
        console.warn("Erro ao buscar livros.");
    }

    try {
        const respostaMetricas = await fetch(`${API_BASE_URL}/vendas/metricas`);
        if (respostaMetricas.ok) {
            const metricas = await respostaMetricas.json();
            faturamentoTotal = metricas.totalFaturado || 0;
            totalVendas = metricas.totalVendas || 0;
            produtoMaisVendido = metricas.produtoMaisVendido || "Sem dados";
            melhorCliente = metricas.melhorCliente || "Sem dados";
        }
    } catch (e) {
        console.warn("Erro ao buscar métricas de vendas.");
    }

    const cardFat = document.getElementById('card-faturamento');
    const cardVendas = document.getElementById('card-total-vendas');
    const cardProd = document.getElementById('card-produto-topo');
    const cardCli = document.getElementById('card-melhor-cliente');

    if (cardFat) cardFat.textContent = `R$ ${faturamentoTotal.toFixed(2).replace('.', ',')}`;
    if (cardVendas) cardVendas.textContent = totalVendas;
    if (cardProd) cardProd.textContent = produtoMaisVendido;
    if (cardCli) cardCli.textContent = melhorCliente;

    const painel = document.getElementById('texto-relatorio');
    if (painel) {
        painel.textContent = `=== RESUMO CONSOLIDADO DA TRÍADE DE PAPEL ===\n\n` +
            `Faturação Total: R$ ${faturamentoTotal.toFixed(2).replace('.', ',')}\n` +
            `Livros Registados: ${totalLivros}\n` +
            `Pedidos Concluídos: ${totalVendas}\n` +
            `Produto Mais Vendido: ${produtoMaisVendido}\n` +
            `Melhor Cliente: ${melhorCliente}` +
            textoAlertaEstoque;
    }
}

async function pesquisarFaturas() {
    const termoElement = document.getElementById('filtro-termo');
    const termo = termoElement ? termoElement.value.trim() : '';
    const caixaResultado = document.getElementById('resultado-faturas');
    if (!caixaResultado) return;

    try {
        let url = termo ? `${API_BASE_URL}/vendas/pesquisar?livro=${encodeURIComponent(termo)}` : `${API_BASE_URL}/vendas/pesquisar`;
        const resposta = await fetch(url);

        if (resposta.ok) {
            const pedidos = await resposta.json();

            if (!pedidos || pedidos.length === 0) {
                caixaResultado.textContent = "=== RESULTADOS ===\nNenhuma fatura encontrada.";
                return;
            }

            let textoHTML = `=== ${pedidos.length} PEDIDO(S) / FATURA(S) ENCONTRADO(S) ===\n\n`;

            pedidos.forEach(p => {
                const idPed = p.idPedido || p.id || 0;
                const dataFormatada = p.dataVenda ? new Date(p.dataVenda).toLocaleString() : 'Data não registada';
                const nomeCliente = p.cliente ? p.cliente.nome : 'Cliente Balcão';
                const docCliente = p.cliente ? p.cliente.cpf : 'N/D';
                const totalVal = p.valorTotal !== undefined ? p.valorTotal : (p.total || 0);
                const totalFatura = totalVal.toFixed(2);
                const formaPag = p.formaPagamento || 'CARTAO';

                textoHTML += `Pedido / Fatura: #${idPed.toString().padStart(5, '0')} | Data: ${dataFormatada}\n`;
                textoHTML += `Cliente: ${nomeCliente} (Doc: ${docCliente})\n`;

                if (p.itens && p.itens.length > 0) {
                    p.itens.forEach(item => {
                        const tituloLivro = item.livro ? item.livro.titulo : 'Livro';
                        const qtd = item.quantidade || 1;
                        const precoUnit = item.precoUnitario || 0;
                        textoHTML += `  - Livro: ${tituloLivro} | Qtd: ${qtd} | Preço Unit.: R$ ${precoUnit.toFixed(2)}\n`;
                    });
                } else {
                    textoHTML += `  - Nenhum item detalhado registado.\n`;
                }

                textoHTML += `Forma de Pagamento: ${formaPag} | Total Geral: R$ ${totalFatura}\n`;
                textoHTML += `----------------------------------------\n`;
            });

            caixaResultado.textContent = textoHTML;
        } else {
            caixaResultado.textContent = "Erro ao pesquisar faturas no servidor.";
        }
    } catch (e) {
        console.error("Erro na pesquisa:", e);
        caixaResultado.textContent = "Erro de conexão com o servidor.";
    }
}

// ==========================================
// FUNÇÕES DO MUNDO LOJA (VITRINE & CARRINHO)
// ==========================================
async function carregarVitrine(listaLivros = null) {
    const vitrine = document.getElementById('vitrine-livros');
    if (!vitrine) return;

    try {
        let livros = listaLivros;
        if (!livros) {
            const resposta = await fetch(`${API_BASE_URL}/livros`);
            livros = await resposta.json();
        }

        vitrine.innerHTML = '';

        livros.forEach(livro => {
            if (livro.quantidadeEstoque > 0) {
                const card = document.createElement('div');
                card.className = 'livro-card';

                const idLivro = livro.idLivro || livro.id;
                const autorNome = obterNomeAutor(livro);

                card.innerHTML = `
                    <div class="livro-capa-container">
                        <img src="${livro.imagem || 'https://via.placeholder.com/150?text=Sem+Capa'}" alt="Capa de ${livro.titulo}" class="livro-capa">
                    </div>
                    <div class="livro-info">
                        <h3 class="livro-titulo">${livro.titulo}</h3>
                        <p class="livro-autor">Por ${autorNome}</p>
                        <p class="livro-tematica-tag">${livro.tematica || ''}</p>
                        <p class="livro-preco">R$ ${livro.preco.toFixed(2).replace('.', ',')}</p>
                    </div>
                    <button class="btn-primario" onclick="adicionarAoCarrinho(${idLivro}, '${livro.titulo.replace(/'/g, "\\'")}', ${livro.preco})">
                        Adicionar ao Carrinho
                    </button>
                `;
                vitrine.appendChild(card);
            }
        });
    } catch (e) {
        vitrine.innerHTML = '<p>Erro ao carregar o catálogo de livros.</p>';
    }
}

async function filtrarLivrosLoja() {
    const inputBusca = document.getElementById('input-busca-loja');
    if (!inputBusca) return;

    const termo = inputBusca.value.toLowerCase().trim();
    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`);
        if (resposta.ok) {
            const livros = await resposta.json();
            const livrosFiltrados = livros.filter(livro => {
                const autorNome = obterNomeAutor(livro).toLowerCase();
                return livro.titulo.toLowerCase().includes(termo) || autorNome.includes(termo);
            });
            carregarVitrine(livrosFiltrados);
        }
    } catch (e) {
        console.error("Erro ao filtrar livros:", e);
    }
}

// ==========================================
// FILTROS RÁPIDOS DA VITRINE
// ==========================================
async function abrirModalTematicas() {
    const modal = document.getElementById('modal-tematicas');
    const containerBotoes = document.getElementById('lista-tematicas-botoes');
    if (!modal || !containerBotoes) return;

    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`);
        if (resposta.ok) {
            const livros = await resposta.json();
            const tematicasUnicas = [...new Set(livros.map(l => l.tematica).filter(t => t))];

            containerBotoes.innerHTML = '';
            if (tematicasUnicas.length === 0) {
                containerBotoes.innerHTML = '<p>Nenhuma temática registada nos livros.</p>';
            } else {
                tematicasUnicas.forEach(tematica => {
                    const btn = document.createElement('button');
                    btn.className = 'btn-primario btn-espaco-inferior';
                    btn.textContent = tematica;
                    btn.onclick = () => filtrarPorTematica(tematica);
                    containerBotoes.appendChild(btn);
                });
            }

            modal.classList.remove('oculto');
        }
    } catch (e) {
        console.error("Erro ao carregar temáticas:", e);
    }
}

function fecharModalTematicas() {
    const modal = document.getElementById('modal-tematicas');
    if (modal) modal.classList.add('oculto');
}

async function filtrarPorTematica(tematicaSelecionada) {
    fecharModalTematicas();
    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`);
        if (resposta.ok) {
            const livros = await resposta.json();
            const filtrados = livros.filter(l => l.tematica === tematicaSelecionada);
            carregarVitrine(filtrados);
            document.getElementById('acervo-seccao').scrollIntoView({ behavior: 'smooth' });
        }
    } catch (e) {
        console.error("Erro ao filtrar por temática:", e);
    }
}

async function filtrarLancamentos() {
    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`);
        if (resposta.ok) {
            const livros = await resposta.json();
            const lancamentos = livros.sort((a, b) => (b.idLivro || b.id) - (a.idLivro || a.id)).slice(0, 6);
            carregarVitrine(lancamentos);
            document.getElementById('acervo-seccao').scrollIntoView({ behavior: 'smooth' });
        }
    } catch (e) {
        console.error("Erro ao filtrar lançamentos:", e);
    }
}

async function filtrarPromocoes() {
    try {
        const resposta = await fetch(`${API_BASE_URL}/livros`);
        if (resposta.ok) {
            const livros = await resposta.json();
            const promocoes = livros.filter(l => l.preco < 40.00);

            if (promocoes.length === 0) {
                alert("De momento não há livros com preço inferior a R$ 40,00.");
                return;
            }

            carregarVitrine(promocoes);
            document.getElementById('acervo-seccao').scrollIntoView({ behavior: 'smooth' });
        }
    } catch (e) {
        console.error("Erro ao filtrar promoções:", e);
    }
}

// ==========================================
// CONTROLO DE LOGIN DO VENDEDOR
// ==========================================
async function realizarLoginVendedor(event) {
    event.preventDefault();
    const docInput = document.getElementById('vendedor-doc');
    const doc = docInput ? docInput.value.trim() : '';

    if (!doc) {
        alert("Por favor, insira o seu CPF ou matrícula.");
        return;
    }

    try {
        localStorage.setItem('vendedorLogadoCPF', doc);

        document.getElementById('vendedor-login-screen').classList.add('oculto');
        document.getElementById('loja-sistema').classList.remove('oculto');

        verificarEstadoSessao();
        carregarVitrine();
        alert("Sessão de vendedor iniciada com sucesso! As próximas vendas ficarão registadas sob a sua responsabilidade.");
    } catch (e) {
        console.error("Erro ao autenticar vendedor:", e);
        alert("Erro ao efetuar login como vendedor.");
    }
}

function abrirModalPerfilVendedor() {
    alert("Perfil do Vendedor - CPF/Matrícula: " + localStorage.getItem('vendedorLogadoCPF'));
}

async function abrirVendasRealizadas() {
    const vendedorDoc = localStorage.getItem('vendedorLogadoCPF');

    if (!vendedorDoc) {
        alert("Acesso restrito! Deve efetuar login como Vendedor.");
        abrirTelaEscolhaLogin();
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/vendas/vendedor/${encodeURIComponent(vendedorDoc)}`);

        if (!response.ok) {
            throw new Error('Erro ao carregar as vendas do vendedor.');
        }

        const pedidos = await response.json();
        const containerVendas = document.getElementById('lista-vendas-vendedor');

        if (containerVendas) {
            if (!pedidos || pedidos.length === 0) {
                containerVendas.innerHTML = '<p class="msg-vazia">Nenhuma venda registada até o momento por este vendedor.</p>';
            } else {
                containerVendas.innerHTML = pedidos.map(p => {
                    const idPed = p.idPedido || p.id || 0;
                    const dataFormatada = p.dataVenda ? new Date(p.dataVenda).toLocaleString() : 'Data não registada';
                    const nomeCliente = p.cliente ? p.cliente.nome : 'Cliente Balcão';
                    const totalVal = p.valorTotal !== undefined ? p.valorTotal : (p.total || 0);
                    const totalFatura = totalVal.toFixed(2).replace('.', ',');
                    const formaPag = p.formaPagamento || 'CARTAO';

                    return `
                        <div class="card-fatura-historico" style="margin-bottom: 12px; padding: 12px; border: 1px solid var(--cor-borda); border-radius: 6px; background: #fff;">
                            <div class="fatura-topo-linha" style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                                <strong class="fatura-id-texto">Pedido #${idPed.toString().padStart(5, '0')}</strong>
                                <span class="fatura-data-texto" style="color: #666; font-size: 0.8rem;">Data: ${dataFormatada}</span>
                            </div>
                            <div class="fatura-corpo-linha" style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <span><strong>Cliente:</strong> ${nomeCliente}</span><br>
                                    <span style="font-size: 0.8rem; color: #666;">Pagamento: ${formaPag}</span>
                                </div>
                                <div class="fatura-total-valor" style="font-weight: bold; color: var(--cor-destaque);">
                                    R$ ${totalFatura}
                                </div>
                            </div>
                        </div>
                    `;
                }).join('');
            }
        }

        ocultarTodosPainels();
        const painelVendas = document.getElementById('app-vendedor-vendas');
        if (painelVendas) painelVendas.classList.remove('oculto');

    } catch (error) {
        console.error("Erro ao carregar vendas do vendedor:", error);
        alert('Não foi possível carregar as vendas realizadas.');
    }
}

// ==========================================
// CADASTRO DE FUNCIONÁRIO (ADMIN)
// ==========================================
async function realizarCadastroVendedor(event) {
    event.preventDefault();

    const tel = document.getElementById('vend-telefone').value.trim();

    const vendedorDTO = {
        cpf: document.getElementById('vend-cpf').value.trim(),
        nome: document.getElementById('vend-nome').value.trim(),
        matricula: document.getElementById('vend-matricula').value.trim(),
        email: document.getElementById('vend-email').value.trim(),
        senha: document.getElementById('vend-senha') ? document.getElementById('vend-senha').value.trim() : '123456',
        dataEntrada: document.getElementById('vend-data').value,
        salario: parseFloat(document.getElementById('vend-salario').value) || 0.0,
        telefones: tel ? [tel] : []
    };

    try {
        const resposta = await fetch(`${API_BASE_URL}/vendedores`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(vendedorDTO)
        });

        if (resposta.ok) {
            alert('🎉 Funcionário registado com sucesso no sistema!');
            document.getElementById('form-cad-vendedor').reset();
        } else {
            const erroMsg = await resposta.text();
            alert('Erro ao registar funcionário: ' + erroMsg);
        }
    } catch (e) {
        console.error("Erro de conexão:", e);
        alert("Não foi possível conectar ao servidor para registar o funcionário.");
    }
}

// ==========================================
// CADASTRO DE CLIENTES (CRIAR CONTA + INDICAÇÃO)
// ==========================================
function abrirModalCadastroCliente() {
    const modal = document.getElementById('modal-cadastro-cliente');
    if (modal) modal.classList.remove('oculto');
}

function fecharModalCadastroCliente() {
    const modal = document.getElementById('modal-cadastro-cliente');
    if (modal) modal.classList.add('oculto');
}

async function realizarCadastroCliente(event) {
    event.preventDefault();

    const tel = document.getElementById('cad-telefone').value.trim();

    const clienteDTO = {
        nome: document.getElementById('cad-nome').value.trim(),
        cpf: document.getElementById('cad-cpf').value.trim(),
        telefones: tel ? [tel] : [],
        email: document.getElementById('cad-email').value.trim(),
        senha: document.getElementById('cad-senha').value.trim(),
        sexo: document.getElementById('cad-sexo').value,
        rua: document.getElementById('cad-rua').value.trim(),
        numero: document.getElementById('cad-numero').value.trim(),
        bairro: document.getElementById('cad-bairro').value.trim(),
        cidade: document.getElementById('cad-cidade').value.trim(),
        uf: document.getElementById('cad-uf').value.trim().toUpperCase(),
        cep: document.getElementById('cad-cep').value.trim(),
        cpfIndicador: document.getElementById('cad-cpf-indicador').value.trim()
    };

    try {
        const resposta = await fetch(`${API_BASE_URL}/clientes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(clienteDTO)
        });

        if (resposta.ok) {
            alert('🎉 Conta criada com sucesso na Livraria Tríade de Papel!');
            document.getElementById('form-cad-cliente').reset();
            fecharModalCadastroCliente();
        } else {
            const erroMsg = await resposta.text();
            alert('Erro ao registar cliente: ' + erroMsg);
        }
    } catch (e) {
        console.error("Erro de conexão:", e);
        alert("Não foi possível conectar ao servidor para realizar o cadastro.");
    }
}

// ==========================================
// ÁREA DO CLIENTE (LOGIN, PERFIL, INDICAÇÕES E HISTÓRICO)
// ==========================================
async function entrarAreaCliente() {
    const identificador = document.getElementById('login-cliente-doc').value.trim();
    const senhaInput = document.getElementById('login-cliente-senha');
    const senha = senhaInput ? senhaInput.value.trim() : '';

    if (!identificador || !senha) {
        alert("Por favor, insira o seu CPF/e-mail e a palavra-passe.");
        return;
    }

    try {
        const resposta = await fetch(`${API_BASE_URL}/auth/login-cliente`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ identificador, senha })
        });

        const dados = await resposta.json();

        if (resposta.ok && dados.sucesso && dados.cliente) {
            const cli = dados.cliente;
            localStorage.setItem('clienteLogadoId', cli.idPessoa);
            localStorage.setItem('clienteLogadoCPF', cli.cpf);
            localStorage.setItem('clienteLogadoNome', cli.nome);
            localStorage.setItem('clienteLogadoEmail', cli.email || 'Não registado');
            localStorage.setItem('clienteLogadoTelefone', (cli.telefones && cli.telefones.length > 0) ? cli.telefones.join(', ') : 'Não registado');
            localStorage.setItem('clienteLogadoEndereco', JSON.stringify({
                rua: cli.rua || '',
                numero: cli.numero || '',
                bairro: cli.bairro || '',
                cidade: cli.cidade || '',
                uf: cli.uf || 'PE',
                cep: cli.cep || '55150-000'
            }));

            document.getElementById('cliente-login-screen').classList.add('oculto');
            document.getElementById('loja-sistema').classList.remove('oculto');

            verificarEstadoSessao();
            carregarVitrine();

            alert(dados.mensagem || "Login efetuado com sucesso!");
        } else {
            alert(dados.mensagem || "CPF/E-mail ou palavra-passe incorretos.");
        }
    } catch (e) {
        console.error("Erro ao validar cliente:", e);
        alert("Erro de conexão com o servidor.");
    }
}

function abrirModalPerfil() {
    const modal = document.getElementById('modal-perfil');
    const detalhes = document.getElementById('perfil-detalhes');
    if (!modal || !detalhes) return;

    const nome = localStorage.getItem('clienteLogadoNome') || 'Cliente Tríade';
    const cpf = localStorage.getItem('clienteLogadoCPF') || 'N/D';
    const email = localStorage.getItem('clienteLogadoEmail') || 'N/D';
    const telefone = localStorage.getItem('clienteLogadoTelefone') || 'N/D';

    detalhes.innerHTML = `
        <p style="margin: 0.25rem 0;"><strong>Nome:</strong> ${nome}</p>
        <p style="margin: 0.25rem 0;"><strong>CPF:</strong> ${cpf}</p>
        <p style="margin: 0.25rem 0;"><strong>E-mail:</strong> ${email}</p>
        <p style="margin: 0.25rem 0;"><strong>Telefone(s):</strong> ${telefone}</p>
    `;

    carregarClientesIndicados();
    modal.classList.remove('oculto');
}

function fecharModalPerfil() {
    const modal = document.getElementById('modal-perfil');
    if (modal) modal.classList.add('oculto');
}

async function registrarIndicacaoCliente(event) {
    event.preventDefault();

    const idIndicador = localStorage.getItem('clienteLogadoId');
    if (!idIndicador) {
        alert("Sessão inválida. Faça login novamente como cliente.");
        return;
    }

    const inputCpf = document.getElementById('input-cpf-indicado');
    const cpfIndicado = inputCpf ? inputCpf.value.trim() : '';

    if (!cpfIndicado) {
        alert("Informe o CPF do amigo que deseja indicar.");
        return;
    }

    try {
        const resposta = await fetch(`${API_BASE_URL}/clientes/${idIndicador}/indicar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cpfIndicado })
        });

        const dados = await resposta.json();
        if (resposta.ok && dados.sucesso) {
            alert("🎉 " + dados.mensagem);
            if (inputCpf) inputCpf.value = '';
            carregarClientesIndicados();
        } else {
            alert(dados.mensagem || "Não foi possível registrar a indicação.");
        }
    } catch (e) {
        console.error("Erro ao registrar indicação:", e);
        alert("Erro de conexão ao registrar indicação.");
    }
}

async function carregarClientesIndicados() {
    const listaEl = document.getElementById('lista-clientes-indicados');
    const idIndicador = localStorage.getItem('clienteLogadoId');
    if (!listaEl || !idIndicador) return;

    try {
        const resposta = await fetch(`${API_BASE_URL}/clientes/${idIndicador}/indicados`);
        if (resposta.ok) {
            const indicados = await resposta.json();
            if (!indicados || indicados.length === 0) {
                listaEl.innerHTML = '<li>Você ainda não indicou nenhum cliente.</li>';
            } else {
                listaEl.innerHTML = indicados
                    .map(ind => `<li><strong>${ind.nome}</strong> (CPF: ${ind.cpf})</li>`)
                    .join('');
            }
        }
    } catch (e) {
        listaEl.innerHTML = '<li>Erro ao carregar lista de indicados.</li>';
    }
}

async function abrirMinhasCompras() {
    const cpfLogado = localStorage.getItem('clienteLogadoCPF');
    if (!cpfLogado) {
        alert("Nenhuma sessão ativa.");
        return;
    }

    ocultarTodosPainels();
    const appCliente = document.getElementById('app-cliente');
    if (appCliente) appCliente.classList.remove('oculto');

    atualizarModoCabecalho('cliente-historico');

    try {
        const resposta = await fetch(`${API_BASE_URL}/vendas/cliente/${encodeURIComponent(cpfLogado)}`);
        if (resposta.ok) {
            const pedidos = await resposta.json();
            exibirHistoricoCliente(pedidos);
        }
    } catch (e) {
        console.error("Erro ao carregar compras:", e);
    }
}

function exibirHistoricoCliente(pedidos) {
    const container = document.getElementById('lista-faturas-cliente');
    if (!container) return;

    container.innerHTML = '';

    if (!pedidos || pedidos.length === 0) {
        container.innerHTML = '<p class="msg-vazia-centro">Ainda não possui compras registadas na sua conta.</p>';
        return;
    }

    pedidos.forEach(p => {
        const idPed = p.idPedido || p.id || 0;
        const dataFormatada = p.dataVenda ? new Date(p.dataVenda).toLocaleString() : 'Data não registada';
        const totalVal = p.valorTotal !== undefined ? p.valorTotal : (p.total || 0);
        const totalFatura = totalVal.toFixed(2).replace('.', ',');
        const formaPag = p.formaPagamento || 'CARTAO';

        let itensHtml = '';
        if (p.itens && p.itens.length > 0) {
            p.itens.forEach(item => {
                const tituloLivro = item.livro ? item.livro.titulo : 'Livro';
                const qtd = item.quantidade || 1;
                const precoUnit = item.precoUnitario || 0;
                itensHtml += `<div class="item-historico-linha">• ${tituloLivro} (Qtd: ${qtd}) - R$ ${(precoUnit * qtd).toFixed(2).replace('.', ',')}</div>`;
            });
        } else {
            itensHtml = '<span class="item-sub-admin">Nenhum item detalhado.</span>';
        }

        const cardFatura = document.createElement('div');
        cardFatura.className = 'card-fatura-historico';

        cardFatura.innerHTML = `
            <div class="fatura-topo-linha">
                <span class="fatura-id-texto">Pedido #${idPed.toString().padStart(5, '0')}</span>
                <span class="fatura-data-texto">Data: ${dataFormatada}</span>
            </div>
            <div class="fatura-corpo-linha">
                <div>
                    <strong class="fatura-itens-titulo">Itens Adquiridos:</strong>
                    <div class="fatura-itens-bloco">${itensHtml}</div>
                    <span class="item-sub-admin">Pagamento: ${formaPag}</span>
                </div>
                <div class="fatura-total-valor">
                    R$ ${totalFatura}
                </div>
            </div>
        `;
        container.appendChild(cardFatura);
    });
}

function logoutCliente() {
    logoutGeral();
}

// ==========================================
// GESTÃO DE CARRINHO E COMPRAS
// ==========================================
function adicionarAoCarrinho(id, titulo, preco) {
    const cpfCliente = localStorage.getItem('clienteLogadoCPF');
    const cpfVendedor = localStorage.getItem('vendedorLogadoCPF');

    if (!cpfCliente && !cpfVendedor) {
        alert("⚠️ Acesso restrito! Deve efetuar login como Cliente ou Vendedor antes de adicionar livros ao carrinho e efetuar compras.");
        document.getElementById('loja-sistema').classList.add('oculto');
        abrirTelaEscolhaLogin();
        return;
    }

    const itemExistente = carrinho.find(item => item.id === id);

    if (itemExistente) {
        itemExistente.quantidade += 1;
    } else {
        carrinho.push({ id, titulo, preco, quantidade: 1 });
    }

    atualizarInterfaceCarrinho();

    const carrinhoLateral = document.getElementById('carrinho-lateral');
    const body = document.body;

    if (carrinhoLateral) {
        carrinhoLateral.classList.remove('oculto');
        setTimeout(() => {
            carrinhoLateral.classList.add('open');
            body.classList.add('cart-open');
        }, 10);
    }
}

function atualizarInterfaceCarrinho() {
    const divItens = document.getElementById('itens-carrinho');
    const spanTotal = document.getElementById('total-carrinho');
    const badge = document.getElementById('contador-carrinho');
    const areaClienteBalcao = document.getElementById('area-cliente-balcao');

    if (areaClienteBalcao) {
        const vendedorLogado = localStorage.getItem('vendedorLogadoCPF');
        const clienteLogado = localStorage.getItem('clienteLogadoCPF');
        if (vendedorLogado && !clienteLogado) {
            areaClienteBalcao.classList.remove('oculto');
        } else {
            areaClienteBalcao.classList.add('oculto');
        }
    }

    if (!divItens) return;

    divItens.innerHTML = '';
    let total = 0;
    let qtdTotal = 0;

    carrinho.forEach((item, index) => {
        total += item.preco * item.quantidade;
        qtdTotal += item.quantidade;

        divItens.innerHTML += `
            <div class="carrinho-item-card">
                <h4 class="carrinho-item-titulo">${item.titulo}</h4>
                <div class="carrinho-item-detalhe">
                    <span class="carrinho-item-preco">R$ ${(item.preco * item.quantidade).toFixed(2).replace('.', ',')}</span>
                    <div class="carrinho-qtd-controlo">
                        <button onclick="alterarQuantidade(${index}, -1)" class="btn-qtd-acao">-</button>
                        <span class="carrinho-qtd-valor">${item.quantidade}</span>
                        <button onclick="alterarQuantidade(${index}, 1)" class="btn-qtd-acao">+</button>
                    </div>
                </div>
            </div>
        `;
    });

    if (spanTotal) spanTotal.textContent = `R$ ${total.toFixed(2).replace('.', ',')}`;
    if (badge) badge.textContent = qtdTotal;
}

function alterarQuantidade(index, delta) {
    carrinho[index].quantidade += delta;

    if (carrinho[index].quantidade <= 0) {
        carrinho.splice(index, 1);
    }

    atualizarInterfaceCarrinho();
}

function toggleCarrinho() {
    const carrinhoLateral = document.getElementById('carrinho-lateral');
    const body = document.body;

    if (!carrinhoLateral) return;

    const estaAberto = carrinhoLateral.classList.contains('open') || !carrinhoLateral.classList.contains('oculto');

    if (estaAberto) {
        carrinhoLateral.classList.remove('open');
        carrinhoLateral.classList.add('oculto');
        body.classList.remove('cart-open');
    } else {
        carrinhoLateral.classList.remove('oculto');
        setTimeout(() => {
            carrinhoLateral.classList.add('open');
            body.classList.add('cart-open');
        }, 10);
    }
}

function alternarPagamento() {
    const metodoInput = document.querySelector('input[name="pagamento"]:checked');
    const areaCartao = document.getElementById('area-cartao');
    const areaPix = document.getElementById('area-pix');

    if (!metodoInput || !areaCartao || !areaPix) return;

    if (metodoInput.value === 'PIX') {
        areaCartao.classList.add('oculto');
        areaPix.classList.remove('oculto');
    } else {
        areaCartao.classList.remove('oculto');
        areaPix.classList.add('oculto');
    }
}

// ==========================================
// ETAPA 1: ABRIR TELA DE CHECKOUT E PRÉ-PREENCHER DADOS
// ==========================================
function abrirCheckoutDetalhado() {
    if (carrinho.length === 0) {
        alert("O seu carrinho está vazio!");
        return;
    }

    const cpfClienteLogado = localStorage.getItem('clienteLogadoCPF');
    const nomeClienteLogado = localStorage.getItem('clienteLogadoNome');
    const vendedorCpf = localStorage.getItem('vendedorLogadoCPF');
    const endSalvo = JSON.parse(localStorage.getItem('clienteLogadoEndereco') || '{}');

    let cpfInicial = cpfClienteLogado || '';
    let nomeInicial = (nomeClienteLogado && nomeClienteLogado !== 'Cliente Tríade') ? nomeClienteLogado : '';

    if (vendedorCpf && !cpfClienteLogado) {
        const inputBalcao = document.getElementById('checkout-cliente-cpf');
        const cpfBalcao = inputBalcao ? inputBalcao.value.trim() : '';

        if (!cpfBalcao) {
            alert("⚠️ Por favor, insira o CPF do cliente no campo do carrinho antes de continuar.");
            if (inputBalcao) inputBalcao.focus();
            return;
        }
        cpfInicial = cpfBalcao;
    }

    if (!cpfInicial && !vendedorCpf) {
        alert("⚠️ Sessão não autenticada. Faça login como Cliente ou Vendedor.");
        toggleCarrinho();
        abrirTelaEscolhaLogin();
        return;
    }

    const metodoPagamentoInput = document.querySelector('input[name="pagamento"]:checked');
    const formaPagamento = metodoPagamentoInput ? metodoPagamentoInput.value : 'CARTAO';
    const numeroCartao = document.getElementById('checkout-cartao') ? document.getElementById('checkout-cartao').value.trim() : '';

    if (formaPagamento === 'CARTAO' && !numeroCartao) {
        alert("Por favor, insira o número do cartão no carrinho antes de continuar.");
        return;
    }

    document.getElementById('checkout-final-cpf').value = cpfInicial;
    document.getElementById('checkout-final-nome').value = nomeInicial;
    document.getElementById('checkout-final-rua').value = endSalvo.rua || '';
    document.getElementById('checkout-final-numero').value = endSalvo.numero || '';
    document.getElementById('checkout-final-bairro').value = endSalvo.bairro || '';
    document.getElementById('checkout-final-cidade').value = endSalvo.cidade || '';
    document.getElementById('checkout-final-uf').value = endSalvo.uf || 'PE';
    if (document.getElementById('checkout-final-cep')) {
        document.getElementById('checkout-final-cep').value = endSalvo.cep || '55150-000';
    }

    const blocoVendedor = document.getElementById('bloco-info-vendedor');
    const spanVendedor = document.getElementById('checkout-vendedor-exibicao');
    if (vendedorCpf && blocoVendedor && spanVendedor) {
        blocoVendedor.classList.remove('oculto');
        spanVendedor.textContent = `CPF / Matrícula: ${vendedorCpf}`;
    } else if (blocoVendedor) {
        blocoVendedor.classList.add('oculto');
    }

    const totalCalculado = carrinho.reduce((acc, item) => acc + (item.preco * item.quantidade), 0);
    document.getElementById('checkout-final-total').textContent = `R$ ${totalCalculado.toFixed(2).replace('.', ',')}`;

    toggleCarrinho();
    document.getElementById('modal-checkout-detalhado').classList.remove('oculto');
}

function fecharCheckoutDetalhado() {
    const modal = document.getElementById('modal-checkout-detalhado');
    if (modal) modal.classList.add('oculto');
}

// ==========================================
// ETAPA 2: CONFIRMAR COMPRA E GERAR FATURA
// ==========================================
async function confirmarCompraFinal(event) {
    event.preventDefault();

    const cpfClienteFinal = document.getElementById('checkout-final-cpf').value.trim();
    const nomeClienteFinal = document.getElementById('checkout-final-nome').value.trim();
    const rua = document.getElementById('checkout-final-rua').value.trim();
    const numero = document.getElementById('checkout-final-numero').value.trim();
    const bairro = document.getElementById('checkout-final-bairro').value.trim();
    const cidade = document.getElementById('checkout-final-cidade').value.trim();
    const uf = document.getElementById('checkout-final-uf').value.trim().toUpperCase();
    const cepEl = document.getElementById('checkout-final-cep');
    const cep = cepEl ? cepEl.value.trim() : '55150-000';
    const vendedorCpf = localStorage.getItem('vendedorLogadoCPF') || null;

    if (!cpfClienteFinal || !nomeClienteFinal || !rua || !numero || !bairro || !cidade || !uf) {
        alert("Por favor, preencha todos os campos obrigatórios de identificação e endereço.");
        return;
    }

    const enderecoFinal = `${rua}, nº ${numero} - ${bairro}, ${cidade} - ${uf} (${cep})`;

    const metodoPagamentoInput = document.querySelector('input[name="pagamento"]:checked');
    const formaPagamento = metodoPagamentoInput ? metodoPagamentoInput.value : 'CARTAO';
    const numeroCartao = document.getElementById('checkout-cartao') ? document.getElementById('checkout-cartao').value.trim() : '';

    const itensDTO = carrinho.map(item => ({
        livroId: item.id,
        quantidade: item.quantidade
    }));

    const pedidoDTO = {
        cpfCliente: cpfClienteFinal,
        nomeCliente: nomeClienteFinal,
        ruaCliente: rua,
        numeroCliente: numero,
        bairroCliente: bairro,
        cidadeCliente: cidade,
        ufCliente: uf,
        cepCliente: cep,
        vendedorIdentificador: vendedorCpf,
        formaPagamento: formaPagamento,
        numeroCartao: numeroCartao,
        itens: itensDTO
    };

    const botaoConfirmar = document.getElementById('btn-confirmar-definitivo');
    if (botaoConfirmar) {
        botaoConfirmar.textContent = "A gerar fatura...";
        botaoConfirmar.disabled = true;
    }

    try {
        const resposta = await fetch(`${API_BASE_URL}/vendas/lote`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pedidoDTO)
        });

        if (resposta.ok) {
            const pedidoCriado = await resposta.json();
            pedidoCriado.enderecoEntrega = enderecoFinal;

            carrinho = [];
            atualizarInterfaceCarrinho();
            fecharCheckoutDetalhado();
            exibirModalFatura(pedidoCriado);
        } else {
            const erroTexto = await resposta.text();
            alert("Erro ao finalizar compra: " + (erroTexto || "Verifique os dados e o stock dos livros."));
        }
    } catch (e) {
        console.error("Erro na requisição de compra:", e);
        alert("Não foi possível conectar ao servidor para concluir a compra.");
    } finally {
        if (botaoConfirmar) {
            botaoConfirmar.textContent = "Confirmar Compra";
            botaoConfirmar.disabled = false;
        }
    }
}

function exibirModalFatura(pedido) {
    const modal = document.getElementById('modal-fatura');
    const corpoFatura = document.getElementById('fatura-body');
    if (!modal || !corpoFatura) return;

    const idPed = pedido.idPedido || pedido.id || 0;
    const dataFormatada = pedido.dataVenda ? new Date(pedido.dataVenda).toLocaleString() : new Date().toLocaleString();
    const nomeCliente = pedido.cliente ? pedido.cliente.nome : 'Cliente Tríade';
    const docCliente = pedido.cliente ? pedido.cliente.cpf : 'N/D';
    const totalVal = pedido.valorTotal !== undefined ? pedido.valorTotal : (pedido.total || 0);
    const totalGeral = totalVal.toFixed(2).replace('.', ',');
    const formaPag = pedido.formaPagamento || 'CARTAO';

    let itensHtml = '';
    if (pedido.itens && pedido.itens.length > 0) {
        pedido.itens.forEach(item => {
            const tituloLivro = item.livro ? item.livro.titulo : 'Livro';
            const qtd = item.quantidade || 1;
            const precoUnit = item.precoUnitario || 0;
            itensHtml += `<li>${tituloLivro} — ${qtd}x R$ ${precoUnit.toFixed(2).replace('.', ',')}</li>`;
        });
    }

    let blocoExtraPagamento = '';
    if (formaPag === 'PIX') {
        const payloadPix = "00020126580514BR.GOV.BCB.PIX...triadepapel";
        const qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=${encodeURIComponent(payloadPix)}`;

        blocoExtraPagamento = `
            <div class="pix-fatura-box" style="text-align: center; margin: 10px 0;">
                <p class="pix-fatura-titulo" style="font-weight: bold; color: var(--cor-destaque); margin-bottom: 8px;">QR Code PIX Gerado com Sucesso</p>
                <img src="${qrCodeUrl}" alt="QR Code PIX" style="width: 150px; height: 150px; margin: 0 auto 10px auto; display: block; border: 1px solid var(--cor-borda); border-radius: 6px; padding: 4px; background: #fff;" />
                <p class="pix-fatura-codigo" style="font-size: 0.75rem; word-break: break-all;">Chave PIX Copia e Cola:<br><code>${payloadPix}</code></p>
            </div>
        `;
    }

    corpoFatura.innerHTML = `
        <div class="fatura-modal-conteudo">
            <p><strong>Pedido / Fatura ID:</strong> #${idPed.toString().padStart(5, '0')}</p>
            <p><strong>Data:</strong> ${dataFormatada}</p>
            <p><strong>Cliente:</strong> ${nomeCliente} (CPF: ${docCliente})</p>
            <p><strong>Endereço:</strong> ${pedido.enderecoEntrega || 'Retirada em Loja'}</p>
            <hr class="fatura-divisor">
            <p><strong>Itens Adquiridos:</strong></p>
            <ul class="fatura-lista-itens">${itensHtml}</ul>
            <p><strong>Forma de Pagamento:</strong> ${formaPag}</p>
            ${blocoExtraPagamento}
            <p class="fatura-total-pago">Total Pago: R$ ${totalGeral}</p>
        </div>
    `;

    modal.classList.remove('oculto');
}

function fecharFatura() {
    const modal = document.getElementById('modal-fatura');
    if (modal) modal.classList.add('oculto');
    carregarVitrine();
}