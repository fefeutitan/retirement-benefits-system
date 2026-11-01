import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type BeneficioStatus = 'EM_ELABORACAO' | 'EM_ANALISE' | 'CONCLUIDO' | 'ARQUIVADO';
export interface Beneficio { id: number; servidorNome: string; servidorCpf: string; servidorOrgao: string; servidorMatricula: string; status: BeneficioStatus; criadoEm: string; }
export interface NovoBeneficio { servidorNome: string; servidorCpf: string; servidorOrgao: string; servidorMatricula: string; }
export type DocumentoCategoria = 'IDENTIFICACAO' | 'VIDA_FUNCIONAL' | 'CONTAGEM_DE_TEMPO' | 'REMUNERACAO_PROVENTOS';
export interface Documento { id: number; nomeArquivo: string; tipoMime: string; tamanho: number; categoria: DocumentoCategoria; anexadoEm: string; }
export interface Tramitacao { id: number; setorOrigem: string; setorDestino: string; responsavel: string; observacao?: string; tramitadoEm: string; }

@Injectable({ providedIn: 'root' })
export class BeneficiosApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/beneficios';
  listar(servidor = ''): Observable<Beneficio[]> { return this.http.get<Beneficio[]>(this.baseUrl, { params: servidor ? { servidor } : {} }); }
  criar(novoBeneficio: NovoBeneficio): Observable<Beneficio> { return this.http.post<Beneficio>(this.baseUrl, novoBeneficio); }
  tramitar(id: number, setorDestino: string, responsavel: string, observacao: string): Observable<unknown> { return this.http.post(`${this.baseUrl}/${id}/tramitacoes`, { setorDestino, responsavel, observacao }); }
  documentos(id: number): Observable<Documento[]> { return this.http.get<Documento[]>(`${this.baseUrl}/${id}/documentos`); }
  tramitacoes(id: number): Observable<Tramitacao[]> { return this.http.get<Tramitacao[]>(`${this.baseUrl}/${id}/tramitacoes`); }
  enviarDocumento(id: number, categoria: DocumentoCategoria, arquivo: File): Observable<Documento> {
    const formData = new FormData(); formData.append('categoria', categoria); formData.append('arquivo', arquivo);
    return this.http.post<Documento>(`${this.baseUrl}/${id}/documentos`, formData);
  }
  conteudoDocumentoUrl(id: number, documentoId: number): string { return `${this.baseUrl}/${id}/documentos/${documentoId}/conteudo`; }
}
