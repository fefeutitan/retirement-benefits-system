import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { Beneficio, BeneficiosApiService, Documento, DocumentoCategoria, Tramitacao } from './beneficios-api.service';

@Component({
  selector: 'app-root',
  imports: [ReactiveFormsModule, RouterOutlet, DatePipe],
  template: `
    <main>
      <header><div><span class="eyebrow">PROCESSO VIRTUAL</span><h1>Beneficios Online</h1></div><button type="button" (click)="carregar()">Atualizar</button></header>
      <section class="card"><h2>Novo beneficio</h2><form [formGroup]="form" (ngSubmit)="criar()"><div class="grid">
        <label>Nome <input formControlName="servidorNome" /></label><label>CPF <input formControlName="servidorCpf" /></label>
        <label>Orgao <input formControlName="servidorOrgao" /></label><label>Matricula <input formControlName="servidorMatricula" /></label>
      </div><button type="submit" [disabled]="form.invalid || salvando()">Criar beneficio</button></form></section>
      @if (erro()) { <p class="error">{{ erro() }}</p> }
      <section class="card"><div class="section-title"><h2>Beneficios cadastrados</h2><span>{{ beneficios().length }}</span></div>
        @if (beneficios().length === 0) { <p>Nenhum beneficio cadastrado.</p> }
        @for (beneficio of beneficios(); track beneficio.id) { <article class="beneficio" [class.selecionado]="selecionado()?.id === beneficio.id" (click)="selecionar(beneficio)"><div><strong>{{ beneficio.servidorNome }}</strong><small>{{ beneficio.servidorOrgao }} - Matricula {{ beneficio.servidorMatricula }}</small></div><span class="status">{{ beneficio.status }}</span></article> }
      </section>

      @if (selecionado(); as beneficio) {
        <section class="processo card"><div class="processo-title"><strong>Processo Virtual</strong><span>{{ beneficio.status }}</span></div><div class="processo-layout"><aside class="arvore"><h3>Pecas do processo</h3>@for (categoria of categorias; track categoria.value) {<div class="categoria"><strong>{{ categoria.label }}</strong>@for (documento of documentosDaCategoria(categoria.value); track documento.id) {<button type="button" [class.ativo]="documentoSelecionado()?.id === documento.id" (click)="visualizar(documento)">&#128196; {{ documento.nomeArquivo }}</button>}</div>}</aside><div class="visualizador"><h2>Dados do processo</h2>@if (previewUrl(); as url) {<iframe [src]="url" title="Pre-visualizacao do documento PDF"></iframe><a class="download" [href]="api.conteudoDocumentoUrl(beneficio.id, documentoSelecionado()!.id)" download>Baixar PDF</a>} @else {<p class="empty-preview">Selecione um documento para visualizar o PDF.</p>}</div></div>
          <div class="section-title"><h2>Adicionar peca</h2><span>{{ documentos().length }} documento(s)</span></div>
          <form [formGroup]="documentoForm" (ngSubmit)="anexarDocumento()" class="upload-form"><select formControlName="categoria"><option value="IDENTIFICACAO">Identificacao</option><option value="VIDA_FUNCIONAL">Vida funcional</option><option value="CONTAGEM_DE_TEMPO">Contagem de tempo</option><option value="REMUNERACAO_PROVENTOS">Remuneracao / proventos</option></select><input type="file" accept="application/pdf" (change)="selecionarArquivo($event)" /><button type="submit" [disabled]="!arquivo() || documentoForm.invalid">Anexar PDF</button></form>
        </section>
        <section class="card"><div class="tabs"><strong>Movimentacoes</strong></div><form [formGroup]="tramitacaoForm" (ngSubmit)="tramitar()" class="grid"><label>Setor destino <input formControlName="setorDestino" /></label><label>Responsavel <input formControlName="responsavel" /></label><label>Observacao <input formControlName="observacao" /></label><button type="submit" [disabled]="tramitacaoForm.invalid">Tramitar processo</button></form><div class="movimentos"><div class="movimento cabecalho"><strong>Data</strong><strong>Local origem</strong><strong>Local destino</strong><strong>Usuario</strong></div>@for (movimento of tramitacoes(); track movimento.id) { <div class="movimento"><span>{{ movimento.tramitadoEm | date:'dd/MM/yyyy HH:mm' }}</span><span>{{ movimento.setorOrigem }}</span><span>{{ movimento.setorDestino }}</span><span>{{ movimento.responsavel }}</span></div> }</div></section>
      }
      <router-outlet />
    </main>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppComponent {
  protected readonly api = inject(BeneficiosApiService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly sanitizer = inject(DomSanitizer);
  protected readonly beneficios = signal<Beneficio[]>([]);
  protected readonly documentos = signal<Documento[]>([]);
  protected readonly tramitacoes = signal<Tramitacao[]>([]);
  protected readonly selecionado = signal<Beneficio | null>(null);
  protected readonly documentoSelecionado = signal<Documento | null>(null);
  protected readonly arquivo = signal<File | null>(null);
  protected readonly erro = signal('');
  protected readonly salvando = signal(false);
  protected readonly previewUrl = computed<SafeResourceUrl | null>(() => { const beneficio = this.selecionado(); const documento = this.documentoSelecionado(); return beneficio && documento ? this.sanitizer.bypassSecurityTrustResourceUrl(this.api.conteudoDocumentoUrl(beneficio.id, documento.id)) : null; });
  protected readonly categorias = [
    { value: 'IDENTIFICACAO' as DocumentoCategoria, label: 'Identificacao' },
    { value: 'VIDA_FUNCIONAL' as DocumentoCategoria, label: 'Vida funcional' },
    { value: 'CONTAGEM_DE_TEMPO' as DocumentoCategoria, label: 'Contagem de tempo' },
    { value: 'REMUNERACAO_PROVENTOS' as DocumentoCategoria, label: 'Remuneracao / proventos' },
  ];
  protected readonly form = this.formBuilder.nonNullable.group({ servidorNome: ['', Validators.required], servidorCpf: ['', [Validators.required, Validators.minLength(11)]], servidorOrgao: ['', Validators.required], servidorMatricula: ['', Validators.required] });
  protected readonly documentoForm = this.formBuilder.nonNullable.group({ categoria: 'IDENTIFICACAO' as DocumentoCategoria });
  protected readonly tramitacaoForm = this.formBuilder.nonNullable.group({ setorDestino: ['', Validators.required], responsavel: ['', Validators.required], observacao: [''] });

  constructor() { this.carregar(); }
  protected carregar(): void { this.api.listar().subscribe({ next: (items) => this.beneficios.set(items), error: () => this.erro.set('Nao foi possivel carregar os beneficios.') }); }
  protected criar(): void { if (this.form.invalid) return; this.salvando.set(true); this.api.criar(this.form.getRawValue()).subscribe({ next: (beneficio) => { this.beneficios.update((items) => [beneficio, ...items]); this.form.reset(); this.salvando.set(false); }, error: () => { this.erro.set('Nao foi possivel criar o beneficio.'); this.salvando.set(false); } }); }
  protected selecionar(beneficio: Beneficio): void { this.selecionado.set(beneficio); this.documentoSelecionado.set(null); this.arquivo.set(null); this.api.documentos(beneficio.id).subscribe((items) => this.documentos.set(items)); this.api.tramitacoes(beneficio.id).subscribe((items) => this.tramitacoes.set(items)); }
  protected documentosDaCategoria(categoria: DocumentoCategoria): Documento[] { return this.documentos().filter((documento) => documento.categoria === categoria); }
  protected visualizar(documento: Documento): void { this.documentoSelecionado.set(documento); }
  protected selecionarArquivo(event: Event): void { const input = event.target as HTMLInputElement; this.arquivo.set(input.files?.[0] ?? null); }
  protected anexarDocumento(): void { const beneficio = this.selecionado(); const arquivo = this.arquivo(); if (!beneficio || !arquivo) return; this.api.enviarDocumento(beneficio.id, this.documentoForm.getRawValue().categoria, arquivo).subscribe({ next: (documento) => { this.documentos.update((items) => [documento, ...items]); this.documentoSelecionado.set(documento); this.arquivo.set(null); }, error: () => this.erro.set('Nao foi possivel anexar o documento PDF.') }); }
  protected tramitar(): void { const beneficio = this.selecionado(); if (!beneficio || this.tramitacaoForm.invalid) return; this.api.tramitar(beneficio.id, ...Object.values(this.tramitacaoForm.getRawValue()) as [string, string, string]).subscribe({ next: () => { this.tramitacaoForm.reset(); this.api.tramitacoes(beneficio.id).subscribe((items) => this.tramitacoes.set(items)); }, error: () => this.erro.set('Nao foi possivel tramitar o processo.') }); }
}
