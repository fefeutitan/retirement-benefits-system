import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { DatePipe } from '@angular/common';
import { Beneficio, BeneficiosApiService, Documento, DocumentoCategoria, Tramitacao } from './beneficios-api.service';

@Component({
  selector: 'app-root',
  imports: [ReactiveFormsModule, RouterOutlet, DatePipe],
  template: `
    <main>
      <header><div><span class="eyebrow">PROCESSO VIRTUAL</span><h1>Benefícios Online</h1></div><button type="button" (click)="carregar()">Atualizar</button></header>
      <section class="card"><h2>Novo benefício</h2><form [formGroup]="form" (ngSubmit)="criar()"><div class="grid">
        <label>Nome <input formControlName="servidorNome" /></label><label>CPF <input formControlName="servidorCpf" /></label>
        <label>Órgão <input formControlName="servidorOrgao" /></label><label>Matrícula <input formControlName="servidorMatricula" /></label>
      </div><button type="submit" [disabled]="form.invalid || salvando()">Criar benefício</button></form></section>
      @if (erro()) { <p class="error">{{ erro() }}</p> }
      <section class="card"><div class="section-title"><h2>Benefícios cadastrados</h2><span>{{ beneficios().length }}</span></div>
        @if (beneficios().length === 0) { <p>Nenhum benefício cadastrado.</p> }
        @for (beneficio of beneficios(); track beneficio.id) { <article class="beneficio" [class.selecionado]="selecionado()?.id === beneficio.id" (click)="selecionar(beneficio)"><div><strong>{{ beneficio.servidorNome }}</strong><small>{{ beneficio.servidorOrgao }} · Matrícula {{ beneficio.servidorMatricula }}</small></div><span class="status">{{ beneficio.status }}</span></article> }
      </section>

      @if (selecionado(); as beneficio) {
        <section class="card"><div class="section-title"><h2>Documentos de {{ beneficio.servidorNome }}</h2><span>{{ documentos().length }}</span></div>
          <form [formGroup]="documentoForm" (ngSubmit)="anexarDocumento()" class="upload-form"><select formControlName="categoria"><option value="IDENTIFICACAO">Identificação</option><option value="VIDA_FUNCIONAL">Vida funcional</option><option value="CONTAGEM_DE_TEMPO">Contagem de tempo</option><option value="REMUNERACAO_PROVENTOS">Remuneração / proventos</option></select><input type="file" accept="application/pdf" (change)="selecionarArquivo($event)" /><button type="submit" [disabled]="!arquivo() || documentoForm.invalid">Anexar PDF</button></form>
          @for (documento of documentos(); track documento.id) { <div class="documento"><span>📄 {{ documento.nomeArquivo }} <small>{{ documento.categoria }} · {{ tamanho(documento.tamanho) }}</small></span><a [href]="api.conteudoDocumentoUrl(beneficio.id, documento.id)" target="_blank">Visualizar</a></div> }
        </section>
        <section class="card"><h2>Tramitação</h2><form [formGroup]="tramitacaoForm" (ngSubmit)="tramitar()" class="grid"><label>Setor destino <input formControlName="setorDestino" /></label><label>Responsável <input formControlName="responsavel" /></label><label>Observação <input formControlName="observacao" /></label><button type="submit" [disabled]="tramitacaoForm.invalid">Tramitar processo</button></form>
          @for (movimento of tramitacoes(); track movimento.id) { <div class="movimento"><strong>{{ movimento.setorOrigem }} → {{ movimento.setorDestino }}</strong><small>{{ movimento.responsavel }} · {{ movimento.tramitadoEm | date:'dd/MM/yyyy HH:mm' }}</small><span>{{ movimento.observacao }}</span></div> }
        </section>
      }
      <router-outlet />
    </main>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppComponent {
  protected readonly api = inject(BeneficiosApiService);
  private readonly formBuilder = inject(FormBuilder);
  protected readonly beneficios = signal<Beneficio[]>([]);
  protected readonly documentos = signal<Documento[]>([]);
  protected readonly tramitacoes = signal<Tramitacao[]>([]);
  protected readonly selecionado = signal<Beneficio | null>(null);
  protected readonly arquivo = signal<File | null>(null);
  protected readonly erro = signal('');
  protected readonly salvando = signal(false);
  protected readonly form = this.formBuilder.nonNullable.group({ servidorNome: ['', Validators.required], servidorCpf: ['', [Validators.required, Validators.minLength(11)]], servidorOrgao: ['', Validators.required], servidorMatricula: ['', Validators.required] });
  protected readonly documentoForm = this.formBuilder.nonNullable.group({ categoria: 'IDENTIFICACAO' as DocumentoCategoria });
  protected readonly tramitacaoForm = this.formBuilder.nonNullable.group({ setorDestino: ['', Validators.required], responsavel: ['', Validators.required], observacao: [''] });

  constructor() { this.carregar(); }
  protected carregar(): void { this.api.listar().subscribe({ next: (items) => this.beneficios.set(items), error: () => this.erro.set('Não foi possível carregar os benefícios.') }); }
  protected criar(): void { if (this.form.invalid) return; this.salvando.set(true); this.api.criar(this.form.getRawValue()).subscribe({ next: (beneficio) => { this.beneficios.update((items) => [beneficio, ...items]); this.form.reset(); this.salvando.set(false); }, error: () => { this.erro.set('Não foi possível criar o benefício.'); this.salvando.set(false); } }); }
  protected selecionar(beneficio: Beneficio): void { this.selecionado.set(beneficio); this.arquivo.set(null); this.api.documentos(beneficio.id).subscribe((items) => this.documentos.set(items)); this.api.tramitacoes(beneficio.id).subscribe((items) => this.tramitacoes.set(items)); }
  protected selecionarArquivo(event: Event): void { const input = event.target as HTMLInputElement; this.arquivo.set(input.files?.[0] ?? null); }
  protected anexarDocumento(): void { const beneficio = this.selecionado(); const arquivo = this.arquivo(); if (!beneficio || !arquivo) return; this.api.enviarDocumento(beneficio.id, this.documentoForm.getRawValue().categoria, arquivo).subscribe({ next: (documento) => { this.documentos.update((items) => [documento, ...items]); this.arquivo.set(null); }, error: () => this.erro.set('Não foi possível anexar o documento PDF.') }); }
  protected tramitar(): void { const beneficio = this.selecionado(); if (!beneficio || this.tramitacaoForm.invalid) return; this.api.tramitar(beneficio.id, ...Object.values(this.tramitacaoForm.getRawValue()) as [string, string, string]).subscribe({ next: () => { this.tramitacaoForm.reset(); this.api.tramitacoes(beneficio.id).subscribe((items) => this.tramitacoes.set(items)); }, error: () => this.erro.set('Não foi possível tramitar o processo.') }); }
  protected tamanho(bytes: number): string { return `${(bytes / 1024 / 1024).toFixed(2)} MB`; }
}
